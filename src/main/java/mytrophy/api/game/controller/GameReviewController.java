package mytrophy.api.game.controller;

import lombok.RequiredArgsConstructor;
import mytrophy.api.game.dto.RequestDTO;
import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.game.entity.GameReview;
import mytrophy.api.game.enums.ReviewStatus;
import mytrophy.api.game.service.GameReviewService;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.global.jwt.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameReviewController {

    private final GameReviewService gameReviewService;
    private final MemberRepository memberRepository;

    //평가남기기 및 업데이트
    @PostMapping("/{appId}/reviews")
    public ResponseEntity<ResponseDTO.GetGameReviewDto> createOrUpdateReview(@PathVariable("appId") Integer appId,
                                                                     @RequestBody RequestDTO.UpdateGameReviewDto dto,
                                                                     @AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        ResponseDTO.GetGameReviewDto reviewDto = gameReviewService.createOrUpdateReview(memberId, appId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto);
    }

    //특정 게임에 대한 모든 평가들 조회하기
    @GetMapping("/{appId}/reviews")
    public ResponseEntity<List<ResponseDTO.GetGameReviewsDto>> getReviewsByAppId(@PathVariable("appId") Integer appId) {
        List<ResponseDTO.GetGameReviewsDto> reviews = gameReviewService.getReviewsByAppId(appId);
        return ResponseEntity.ok(reviews);
    }

    //내가 평가남긴 게임 조회하기
    @GetMapping("/reviews/my")
    public ResponseEntity<List<ResponseDTO.GetGameReviewDto>> getReviewsByMemberId(@AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        List<ResponseDTO.GetGameReviewDto> reviews = gameReviewService.getReviewsByMemberId(memberId);
        return ResponseEntity.ok(reviews);
    }

    //내가 추천한 게임 조회하기
    @GetMapping("reviews/my/recommended")
    public ResponseEntity<List<ResponseDTO.GetGameReviewDto>> getRecommendedGames(@AuthenticationPrincipal CustomUserDetails userinfo) {
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        List<ResponseDTO.GetGameReviewDto> recommendedGames = gameReviewService.getRecommendedGamesByMemberId(memberId);
        return ResponseEntity.ok(recommendedGames);
    }
}
