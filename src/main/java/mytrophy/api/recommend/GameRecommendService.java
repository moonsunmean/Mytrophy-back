package mytrophy.api.recommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.GameCategory;
import mytrophy.api.game.entity.GameReview;
import mytrophy.api.game.enums.ReviewStatus;
import mytrophy.api.game.repository.GameRepository;
import mytrophy.api.game.repository.GameReviewRepository;
import mytrophy.api.member.entity.MemberCategory;
import mytrophy.api.member.repository.MemberCategoryRepository;
import mytrophy.global.handler.CustomException;
import mytrophy.global.handler.ErrorCodeEnum;
import mytrophy.global.util.CosineSimilarityUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameRecommendService {

    private final GameReviewRepository gameReviewRepository;
    private final GameRepository gameRepository;
    private final MemberCategoryRepository memberCategoryRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Page<ResponseDTO.GetRecommendedGameDto> recommendGamesForMember(Long memberId, Pageable pageable) {
        // 사용자 리뷰 및 선호 카테고리 조회
        List<GameReview> userReviews = gameReviewRepository.findByMemberId(memberId);
        List<MemberCategory> preferredCategories = memberCategoryRepository.findByMemberId(memberId);

        if (userReviews.isEmpty() && preferredCategories.isEmpty()) {
            return Page.empty(pageable); // 사용자의 평가와 선호 카테고리가 모두 없을 경우 빈 페이지 반환
        }

        // 사용자 평가 기반으로 프로파일 벡터 생성
        Map<Game, Double> gameRatings = new HashMap<>();
        for (GameReview review : userReviews) {
            double weight = getWeightByReviewStatus(review.getReviewStatus());
            Game game = review.getGame();
            try {
                double[] embedding = getGameEmbedding(game);
                if (embedding != null) {
                    gameRatings.put(game, weight);
                }
            } catch (CustomException e) {
                // 게임 임베딩 벡터가 null이거나 비어 있을 경우 무시
            }
        }

        double[] userProfile;
        try {
            userProfile = calculateUserProfile(gameRatings);
        } catch (CustomException e) {
            return Page.empty(pageable);
        }

        List<Game> sampleGames = gameRepository.findSampleGames();

        // 모든 게임에 대해 코사인 유사도 계산해서 추천
        List<ResponseDTO.GetRecommendedGameDto> recommendedGames = sampleGames.stream()
                .filter(game -> !gameRatings.containsKey(game)) // 사용자가 이미 평가한 게임 제외
                .filter(game -> {
                    try {
                        return getGameEmbedding(game) != null;
                    } catch (IllegalArgumentException e) {
                        return false; // 임베딩 벡터가 null이거나 비어 있는 게임 제외
                    }
                })
                .map(game -> {
                    double similarity = CosineSimilarityUtil.cosineSimilarity(userProfile, getGameEmbedding(game));
                    double categoryBoost = getCategoryBoost(game, preferredCategories);
                    return new AbstractMap.SimpleEntry<>(game, similarity + categoryBoost);
                })
                .sorted(Map.Entry.<Game, Double>comparingByValue().reversed())
                .limit(pageable.getPageSize())
                .map(Map.Entry::getKey)
                .map(this::toGetRecommendedGameDto)
                .collect(Collectors.toList());

        return new PageImpl<>(recommendedGames, pageable, recommendedGames.size());
    }

    //평가에 따라 가중치 반환
    private double getWeightByReviewStatus(ReviewStatus reviewStatus) {
        return switch (reviewStatus) {
            case PERFECT -> 1.0;
            case GOOD -> 0.7;
            case BAD -> 0.3;
            default -> 0.5;
        };
    }

    //사용자가 평가한 게임들의 임베딩 벡터를 기반으로 사용자 프로파일 벡터 생성
    @Transactional(readOnly = true)
    private double[] calculateUserProfile(Map<Game, Double> gameRatings) {
        if (gameRatings.isEmpty()) {
            throw new CustomException(ErrorCodeEnum.NOT_FOUND_GAME_REVIEW);
        }

        // 첫 번째 게임의 임베딩 벡터를 가져와 길이를 확인
        double[] sampleEmbedding = getGameEmbedding(gameRatings.keySet().iterator().next());
        double[] userProfile = new double[sampleEmbedding.length];
        for (Map.Entry<Game, Double> entry : gameRatings.entrySet()) {
            double[] gameEmbedding = getGameEmbedding(entry.getKey());
            if (gameEmbedding == null) {
                continue;
            }
            for (int i = 0; i < userProfile.length; i++) {
                userProfile[i] += gameEmbedding[i] * entry.getValue();
            }
        }
        return userProfile;
    }

    //게임의 평균 임베딩 벡터 가져오기
    @Cacheable("gameEmbeddings")
    private double[] getGameEmbedding(Game game) {
        String embeddingVector = game.getAverageEmbeddingVector();
        if (embeddingVector == null || embeddingVector.isEmpty()) {
            throw new CustomException(ErrorCodeEnum.EMPTY_EMBEDDING_VECTOR);
        }
        try {
            return objectMapper.readValue(embeddingVector, double[].class);
        } catch (Exception e) {
            throw new CustomException(ErrorCodeEnum.FAILED_TO_PARSING_EMBEDDING_VECTOR);
        }
    }


    //게임의 카테고리가 사용자의 선호 카테고리와 일치하는지 확인하고, 일치할 경우 개수에 따라 추가적인 가중치 부여.
    private double getCategoryBoost(Game game, List<MemberCategory> preferredCategories) {
        double baseBoost = 0.1; // 기본 부스트 값
        int preferredCategoryCount = preferredCategories.size(); // 사용자가 선택한 선호 카테고리 개수

        // 선호 카테고리 ID를 Set으로 변환
        Set<Long> preferredCategoryIds = preferredCategories.stream()
                .map(memberCategory -> memberCategory.getCategory().getId())
                .collect(Collectors.toSet());

        // 게임 카테고리 ID를 Set으로 변환
        Set<Long> gameCategoryIds = game.getGameCategoryList().stream()
                .map(gameCategory -> gameCategory.getCategory().getId())
                .collect(Collectors.toSet());

        // 교집합의 크기를 계산
        gameCategoryIds.retainAll(preferredCategoryIds);
        int matchingCategoryCount = gameCategoryIds.size();

        if (preferredCategoryCount == 0) {
            return 0; // 선호 카테고리가 없으면 부스트를 적용하지 않음
        }

        // 일치하는 카테고리 개수에 따라 가중치 조정
        return baseBoost * matchingCategoryCount / preferredCategoryCount;
    }

    private ResponseDTO.GetRecommendedGameDto toGetRecommendedGameDto(Game game) {
        return new ResponseDTO.GetRecommendedGameDto(
                game.getAppId(),
                game.getName(),
                game.getDescription(),
                game.getDeveloper(),
                game.getPublisher(),
                game.getRequirement(),
                game.getPrice(),
                game.getReleaseDate(),
                game.getRecommendation(),
                game.getPositive(),
                game.getHeaderImagePath(),
                game.getKoIsPossible(),
                game.getEnIsPossible(),
                game.getJpIsPossible(),
                game.getGameCategoryList().stream()
                        .map(cat -> new ResponseDTO.GetGameCategoryDTO(cat.getCategory().getId(), cat.getCategory().getName()))
                        .collect(Collectors.toList())
        );
    }
}
