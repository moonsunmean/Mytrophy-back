package mytrophy.api.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import mytrophy.api.member.service.MemberGameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberGameController {

    private final MemberGameService memberSteamService;

    public MemberGameController(MemberGameService memberSteamService) {
        this.memberSteamService = memberSteamService;
    }

    // 회원 게임 목록 조회
    @GetMapping("/{id}/mygames")
    public ResponseEntity<JsonNode> getMemberSteamGamesFromApi(@PathVariable("id") Long id) {
        try {
            JsonNode steamGames = memberSteamService.findMemberSteamGames(id);
            return ResponseEntity.ok(steamGames);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원 게임 목록 저장
    @PostMapping("/{id}/mygames/save")
    public ResponseEntity<String> readMemberSteamGames(@PathVariable("id") Long id) {
        try {
            memberSteamService.saveMemberSteamGameList(id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("게임 목록이 저장 되었습니다");
    }

    // 회원 보유 게임 상세 조회
    @GetMapping("/{id}/mygames/{appId}")
    public ResponseEntity<JsonNode> getMemberSteamGameAchievementFromApi(@PathVariable("id") Long id, @PathVariable("appId") Long appId) {
        try {
            JsonNode steamGameDetail = memberSteamService.findMemberSteamGameAchievement(id, appId);
            return ResponseEntity.ok(steamGameDetail);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
