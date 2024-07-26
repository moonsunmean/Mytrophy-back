package mytrophy.api.recommend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.global.jwt.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class GameRecommendController {

    private final GameRecommendService gameRecommendService;
    private final MemberRepository memberRepository;

    @Operation(summary = "맞춤형 추천게임 조회", description = "회원의 관심 카테고리, 게임 평가를 기반으로 추천된 게임을 조회합니다.")
    @GetMapping("/recommendations")
    public ResponseEntity<Page<ResponseDTO.GetRecommendedGameDto>> getRecommendations(
            @AuthenticationPrincipal CustomUserDetails userinfo,
            @Parameter(description = "페이지 번호", required = true) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "한 페이지의 데이터 개수", required = true) @RequestParam(defaultValue = "10") int size) {

        Long memberId = memberRepository.findByUsername(userinfo.getUsername()).getId();
        Pageable pageable = PageRequest.of(page, size);

        Page<ResponseDTO.GetRecommendedGameDto> recommendedGames = gameRecommendService.recommendGamesForMember(memberId, pageable);

        return ResponseEntity.ok(recommendedGames);
    }
}
