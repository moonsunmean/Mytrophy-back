package mytrophy.api.game.service;

import lombok.RequiredArgsConstructor;
import mytrophy.api.game.dto.RequestDTO;
import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.GameReview;
import mytrophy.api.game.enums.ReviewStatus;
import mytrophy.api.game.repository.GameRepository;
import mytrophy.api.game.repository.GameReviewRepository;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.global.handler.CustomException;
import mytrophy.global.handler.ErrorCodeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GameReviewServiceImpl implements GameReviewService {

    private final GameReviewRepository gameReviewRepository;
    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;

    //평가 남기기 및 업데이트
    @Override
    public ResponseDTO.GetGameReviewDto createOrUpdateReview(Long memberId, Integer appId, RequestDTO.UpdateGameReviewDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_MEMBER_ID));
        Game game = gameRepository.findByAppId(appId);
        if (game == null) {
            throw new CustomException(ErrorCodeEnum.NOT_EXISTS_GAME_ID);
        }

        GameReview review = gameReviewRepository.findByMemberAndGame(member, game)
                .orElse(GameReview.builder()
                        .member(member)
                        .game(game)
                        .reviewStatus(ReviewStatus.valueOf(dto.getReviewStatus()))
                        .build());

        review.updateReviewStatus(ReviewStatus.valueOf(dto.getReviewStatus()));
        gameReviewRepository.save(review);

        return toGetGameReviewDto(review);
    }

    //특정 회원이 평가를 남긴 게임들
    @Override
    public List<ResponseDTO.GetGameReviewDto> getReviewsByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_MEMBER_ID));

        List<GameReview> reviews = gameReviewRepository.findByMember(member);
        if (reviews.isEmpty()) {
             throw new CustomException(ErrorCodeEnum.NOT_REVIEWED);
        }

        return reviews.stream()
                .map(this::toGetGameReviewDto)
                .collect(Collectors.toList());
    }

    //특정 게임이 받은 평가들 조회
    @Override
    public List<ResponseDTO.GetGameReviewsDto> getReviewsByAppId(Integer appId) {
        Game game = gameRepository.findByAppId(appId);
        if (game == null) {
            throw new CustomException(ErrorCodeEnum.NOT_EXISTS_GAME_ID);
        }

        List<GameReview> reviews = gameReviewRepository.findByGame(game);
        if (reviews.isEmpty()) {
            throw new CustomException(ErrorCodeEnum.NOT_REVIEWED);
        }

        return reviews.stream()
                .map(review -> new ResponseDTO.GetGameReviewsDto(review.getMember().getId(), review.getReviewStatus().name()))
                .collect(Collectors.toList());
    }

    //내가 추천한 게임들 (ReviewStatus가 PERFECT)
    @Override
    public List<ResponseDTO.GetGameReviewDto> getRecommendedGamesByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_MEMBER_ID));

        List<GameReview> perfectReviews = gameReviewRepository.findByMemberAndReviewStatus(member, ReviewStatus.PERFECT);

        return perfectReviews.stream()
                .map(this::toGetGameReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseDTO.GetGameReviewsDto getMyReviewByAppId(Long memberId, Integer appId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_MEMBER_ID));
        Game game = gameRepository.findByAppId(appId);
        if (game == null) {
            throw new CustomException(ErrorCodeEnum.NOT_EXISTS_GAME_ID);
        }

        Optional<GameReview> review = gameReviewRepository.findByMemberAndGame(member, game);

        return review.map(r -> new ResponseDTO.GetGameReviewsDto(member.getId(), r.getReviewStatus().name()))
                .orElse(null);
    }

    private ResponseDTO.GetGameReviewDto toGetGameReviewDto(GameReview review) {
        Game game = review.getGame();
        return new ResponseDTO.GetGameReviewDto(
                game.getAppId(),
                review.getMember().getId(),
                review.getReviewStatus().name(),
                game.getName(),
                game.getDescription(),
                game.getPrice(),
                game.getRecommendation(),
                game.getPositive(),
                game.getHeaderImagePath(),
                game.getKoIsPosible(),
                game.getGameCategoryList().stream()
                        .map(cat -> new ResponseDTO.GetGameCategoryDTO(cat.getCategory().getId(), cat.getCategory().getName()))
                        .collect(Collectors.toList())
        );
    }
}
