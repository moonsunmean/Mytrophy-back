package mytrophy.api.game.service;

import mytrophy.api.game.dto.RequestDTO;
import mytrophy.api.game.dto.ResponseDTO;

import java.util.List;

public interface GameReviewService {

    ResponseDTO.GetGameReviewDto createOrUpdateReview(Long memberId, Integer appId, RequestDTO.UpdateGameReviewDto dto);

    List<ResponseDTO.GetGameReviewDto> getReviewsByMemberId(Long memberId);

    List<ResponseDTO.GetGameReviewsDto> getReviewsByAppId(Integer appId);

    List<ResponseDTO.GetGameReviewDto> getRecommendedGamesByMemberId(Long memberId);
}
