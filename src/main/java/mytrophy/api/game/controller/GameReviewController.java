package mytrophy.api.game.controller;

import lombok.RequiredArgsConstructor;
import mytrophy.api.game.dto.RequestDTO;
import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.service.GameRecommendService;
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
    @GetMapping("/myreviews")
    public ResponseEntity<List<ResponseDTO.GetGameReviewDto>> getReviewsByMemberId(@AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        List<ResponseDTO.GetGameReviewDto> reviews = gameReviewService.getReviewsByMemberId(memberId);
        return ResponseEntity.ok(reviews);
    }

    //내가 추천한 게임 조회하기
    @GetMapping("reviews/myrecommended")
    public ResponseEntity<List<ResponseDTO.GetGameReviewDto>> getRecommendedGames(@AuthenticationPrincipal CustomUserDetails userinfo) {
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        List<ResponseDTO.GetGameReviewDto> recommendedGames = gameReviewService.getRecommendedGamesByMemberId(memberId);
        return ResponseEntity.ok(recommendedGames);
    }

    //특정게임에 내가 남긴 리뷰
    @GetMapping("/{appId}/myreview")
    public ResponseEntity<ResponseDTO.GetGameReviewsDto> getMyReviewByAppId(@PathVariable("appId") Integer appId,
                                                                            @AuthenticationPrincipal CustomUserDetails userinfo) {
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        ResponseDTO.GetGameReviewsDto reviewDto = gameReviewService.getMyReviewByAppId(memberId, appId);
        return ResponseEntity.ok(reviewDto);
    }

    @GetMapping("/recommendations")
    public Page<ResponseDTO.GetGameDetailDTO> getRecommendations(@AuthenticationPrincipal CustomUserDetails userinfo,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        Pageable pageable = PageRequest.of(page, size);
        return gameRecommendService.recommendGamesForMember(memberId, pageable);
    }
}
