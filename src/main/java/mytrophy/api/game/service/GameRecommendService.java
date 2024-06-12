package mytrophy.api.game.service;

import lombok.RequiredArgsConstructor;
import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.game.entity.Category;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.GameCategory;
import mytrophy.api.game.entity.GameReview;
import mytrophy.api.game.enums.ReviewStatus;
import mytrophy.api.game.repository.GameRepository;
import mytrophy.api.game.repository.GameReviewRepository;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.global.handler.CustomException;
import mytrophy.global.handler.ErrorCodeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class GameRecommendService {

    private final MemberRepository memberRepository;
    private final GameReviewRepository gameReviewRepository;
    private final GameRepository gameRepository;

    public Page<ResponseDTO.GetGameReviewDto> recommendGames(Long memberId, Pageable pageable) {
        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_MEMBER_ID));

        List<Category> preferredCategories = member.getCategories(); // 회원의 관심 카테고리

        // 회원이 남긴 BAD 및 PERFECT 리뷰의 게임 카테고리를 추출
        List<GameReview> memberReviews = gameReviewRepository.findByMember(member);

        // BAD 리뷰의 게임 카테고리 ID 집합
        Set<Long> badReviewCategoryIds = memberReviews.stream()
                .filter(review -> review.getReviewStatus() == ReviewStatus.BAD)
                .flatMap(review -> review.getGame().getGameCategoryList().stream())
                .map(gameCategory -> gameCategory.getCategory().getId())
                .collect(Collectors.toSet());

        // PERFECT 리뷰의 게임 카테고리 ID 집합 (상위 10개 또는 10개 이하)
        Set<Long> perfectReviewCategoryIds = memberReviews.stream()
                .filter(review -> review.getReviewStatus() == ReviewStatus.PERFECT)
                .flatMap(review -> review.getGame().getGameCategoryList().stream())
                .map(gameCategory -> gameCategory.getCategory().getId())
                .distinct()
                .limit(10)
                .collect(Collectors.toSet());

        // 모든 게임을 가져온 후 관심 카테고리에 포함되고 BAD 리뷰 카테고리에 속하지 않은 게임만 필터링
        List<Game> games = gameRepository.findAll();

        // 필터링된 게임 리스트 생성
        List<Game> recommendedGames = games.stream()
                .filter(game -> {
                    // 게임의 카테고리 ID 집합 생성
                    Set<Long> gameCategoryIds = game.getGameCategoryList().stream()
                            .map(GameCategory::getCategory)
                            .map(Category::getId)
                            .collect(Collectors.toSet());

                    // BAD 카테고리를 제외하고 관심 카테고리에 포함된 게임
                    boolean isPreferred = gameCategoryIds.stream()
                            .anyMatch(categoryId -> preferredCategories.stream()
                                    .anyMatch(category -> category.getId().equals(categoryId))
                                    && !badReviewCategoryIds.contains(categoryId));

                    // PERFECT 카테고리에 속한 게임
                    boolean isPerfect = gameCategoryIds.stream()
                            .anyMatch(perfectReviewCategoryIds::contains);

                    return isPreferred || isPerfect;
                })
                .collect(Collectors.toList());

        // 회원이 평점을 남긴 게임의 평균 평점을 계산
        Map<Long, Double> gameRatingMap = memberReviews.stream()
                .collect(Collectors.groupingBy(review -> review.getGame().getId(),
                        Collectors.averagingDouble(review -> {
                            switch (review.getReviewStatus()) {
                                case PERFECT:
                                    return 5.0;
                                case GOOD:
                                    return 3.0;
                                case BAD:
                                    return 1.0;
                                default:
                                    return 0.0;
                            }
                        })));

        // 게임 리스트를 평점에 따라 정렬
        recommendedGames.sort((game1, game2) -> {
            double rating1 = gameRatingMap.getOrDefault(game1.getId(), Double.MAX_VALUE); // 평점 없는 게임은 최하위로
            double rating2 = gameRatingMap.getOrDefault(game2.getId(), Double.MAX_VALUE);
            return Double.compare(rating2, rating1); // 높은 평점 순으로 정렬
        });

        List<ResponseDTO.GetGameReviewDto> gameReviewDtos = recommendedGames.stream()
                .map(game -> {
                    Optional<GameReview> review = gameReviewRepository.findByMemberAndGame(member, game);
                    return new ResponseDTO.GetGameReviewDto(
                            game.getAppId(),
                            member.getId(),
                            review.map(r -> r.getReviewStatus().name()).orElse(null),
                            game.getName(),
                            game.getDescription(),
                            game.getPrice(),
                            game.getRecommendation(),
                            game.getPositive(),
                            game.getHeaderImagePath(),
                            game.getKoIsPosible(),
                            game.getGameCategoryList().stream()
                                    .map(gc -> new ResponseDTO.GetGameCategoryDTO(gc.getCategory().getId(), gc.getCategory().getName()))
                                    .collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());

        // 페이지네이션 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), gameReviewDtos.size());
        List<ResponseDTO.GetGameReviewDto> pageContent = gameReviewDtos.subList(start, end);

        return new PageImpl<>(pageContent, pageable, gameReviewDtos.size());
    }
}
