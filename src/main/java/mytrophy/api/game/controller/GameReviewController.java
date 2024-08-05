package mytrophy.api.game.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import mytrophy.api.game.dto.RequestDTO;
import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.recommend.GameRecommendService;
import mytrophy.api.game.service.GameReviewService;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.global.jwt.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final GameRecommendService gameRecommendService;

    //평가남기기 및 업데이트
    @Operation(summary = "게임 평가 등록", description = "특정 게임에 대한 평가를 갱신합니다. (\"BAD\", \"GOOD\", \"PERFECT\")")
    @PostMapping("/{appId}/reviews")
    public ResponseEntity<ResponseDTO.GetGameReviewDto> createOrUpdateReview(@Parameter(description = "평가를 남기고자 하는 게임의 APP_ID", required = true) @PathVariable("appId") Integer appId,
                                                                             @RequestBody RequestDTO.UpdateGameReviewDto dto,
                                                                             @AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        ResponseDTO.GetGameReviewDto reviewDto = gameReviewService.createOrUpdateReview(memberId, appId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto);
    }

    //특정 게임에 대한 모든 평가들 조회하기
    @Operation(summary = "특정 게임의 모든 평가 조회", description = "특정 게임에 남겨진 모든 평가를 조회합니다.")
    @GetMapping("/{appId}/reviews")
    public ResponseEntity<List<ResponseDTO.GetGameReviewsDto>> getReviewsByAppId(@Parameter(description = "조회하고자 하는 게임의 APP_ID", required = true) @PathVariable("appId") Integer appId) {
        List<ResponseDTO.GetGameReviewsDto> reviews = gameReviewService.getReviewsByAppId(appId);
        return ResponseEntity.ok(reviews);
    }

    //내가 평가남긴 게임 조회하기
    @Operation(summary = "본인이 평가한 게임목록 조회", description = "본인이 평가한 게임의 목록을 조회합니다.")
    @GetMapping("/my-reviews")
    public ResponseEntity<List<ResponseDTO.GetGameReviewDto>> getReviewsByMemberId(@AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        List<ResponseDTO.GetGameReviewDto> reviews = gameReviewService.getReviewsByMemberId(memberId);
        return ResponseEntity.ok(reviews);
    }

    //내가 추천한 게임 조회하기
    @Operation(summary = "본인이 좋은 평가를 남긴 게임목록 조회", description = "본인이 좋은 평가를 남긴 게임목록을 조회합니다 (reviewStatus = \"PERFECT\")")
    @GetMapping("reviews/my-recommended")
    public ResponseEntity<List<ResponseDTO.GetGameReviewDto>> getRecommendedGames(@AuthenticationPrincipal CustomUserDetails userinfo) {
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        List<ResponseDTO.GetGameReviewDto> recommendedGames = gameReviewService.getRecommendedGamesByMemberId(memberId);
        return ResponseEntity.ok(recommendedGames);
    }

    //특정게임에 내가 남긴 리뷰
    @Operation(summary = "특정 게임에 본인이 남긴 평가 조회", description = "특정 게임에 본인이 남긴 평가를 조회합니다.")
    @GetMapping("/{appId}/my-review")
    public ResponseEntity<ResponseDTO.GetGameReviewsDto> getMyReviewByAppId(@Parameter(description = "조회하고자 하는 게임의 APP_ID", required = true) @PathVariable("appId") Integer appId,
                                                                            @AuthenticationPrincipal CustomUserDetails userinfo) {
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        ResponseDTO.GetGameReviewsDto reviewDto = gameReviewService.getMyReviewByAppId(memberId, appId);
        return ResponseEntity.ok(reviewDto);
    }
}
