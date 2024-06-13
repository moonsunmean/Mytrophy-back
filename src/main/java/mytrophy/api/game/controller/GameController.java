package mytrophy.api.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.service.ArticleService;
import mytrophy.api.game.dto.RequestDTO.UpdateGameRequestDTO;
import mytrophy.api.game.dto.RequestDTO.UpdateGameCategoryDTO;
import mytrophy.api.game.dto.RequestDTO.SearchGameRequestDTO;
import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameCategoryDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGamePlayerNumberDTO;
import mytrophy.api.game.dto.ResponseDTO.GetTopGameDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameDetailDTO;
import mytrophy.api.game.service.GameDataService;
import mytrophy.api.game.service.GameService;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.service.MemberService;
import mytrophy.global.jwt.CustomUserDetails;
import mytrophy.global.scheduler.GameDataScheduler;
import org.hibernate.mapping.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;
    private final GameDataService gameDataService;
    private final GameDataScheduler gameDataScheduler;

    @Autowired
    public GameController(GameService gameService, GameDataService gameDataService, GameDataScheduler gameDataScheduler, ArticleService articleService, MemberService memberService) {
        this.gameService = gameService;
        this.gameDataService = gameDataService;
        this.gameDataScheduler = gameDataScheduler;
    }


    @GetMapping
    @Operation(summary = "게임 목록 조회",description = "게임 목록을 조회합니다.")
    public ResponseEntity<Page<GetGameDetailDTO>> getAllGame(@Parameter(description = "페이지 번호") @RequestParam(name = "page", defaultValue = "1") int page,
                                                          @Parameter(description = " 한 페이지의 데이터 개수") @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(gameService.getAllGameDTO(page - 1, size));
    }

    @GetMapping("/{appId}")
    @Operation(summary = "게임 조회",description = "게임 하나를 조회합니다.")
    public ResponseEntity<GetGameDetailDTO> getGameDetail(@Parameter(required = true,description = "게임 일련번호") @PathVariable(name = "appId") Integer appId) {

        return ResponseEntity.ok(gameService.getGameDetailDTO(appId));
    }

    @PatchMapping("/{appId}")
    @Operation(summary = "게임 업데이트",description = "게임 하나를 업데이트합니다.")
    public ResponseEntity<String> updateGameDetail(@Parameter(required = true,description = "게임 일련번호") @PathVariable(name = "appId") Integer appId,@RequestBody UpdateGameRequestDTO updateGameRequestDTO) {
        if (gameService.updateGameDetail(appId,updateGameRequestDTO)) {
            return ResponseEntity.ok("게임정보 수정에 성공했습니다.");
        }
        return ResponseEntity.badRequest().body("게임정보 수정에 실패했습니다.");
    }

    @DeleteMapping("/{appId}")
    @Operation(summary = "게임 삭제",description = "게임 하나를 삭제합니다.")
    public ResponseEntity<String> deleteGameDetail(@Parameter(required = true,description = "게임 일련번호") @PathVariable("appId") Integer appId) {
        if (gameService.deleteGameDetail(appId)) {
            return ResponseEntity.ok("게임정보 삭제에 성공했습니다.");
        }
        return ResponseEntity.badRequest().body("게임정보 삭제에 실패했습니다.");
    }

    @PostMapping("/search")
    @Operation(summary = "게임 조회",description = "사용자가 입력한 조건으로 게임 목록을 조회합니다.")
    public ResponseEntity<Page<GetGameDetailDTO>> getSearchGame(@Parameter(required = true,description = "게임 조회 요청") @RequestBody SearchGameRequestDTO searchGameRequestDTO) {

        return ResponseEntity.ok(gameService.getSearchGameDTO(searchGameRequestDTO));
    }

    @GetMapping("/top100")
    @Operation(summary = "TOP100 게임 조회",description = "스팀의 2주간의 TOP100 목록을 조회합니다.")
    public ResponseEntity<Page<GetTopGameDTO>> getTopGame(
            @Parameter(description = "페이지 번호") @RequestParam(name = "page", defaultValue = "1") int page,
            @Parameter(description = " 한 페이지의 데이터 개수") @RequestParam(name = "size", defaultValue = "10") int size) throws JsonProcessingException {

        return ResponseEntity.ok(gameService.getTopGameDTO(page-1,size,gameDataService.receiveTopSteamGameList(100,"request")));
    }

    @GetMapping("/release")
    @Operation(summary = "최신게임 조회",description = "최신 출시 게임 기준으로 게임 목록을 조회합니다.")
    public ResponseEntity<Page<GetGameDetailDTO>> getReleaseSortGame(
            @Parameter(description = "페이지 번호") @RequestParam(name = "page", defaultValue = "1") int page,
            @Parameter(description = " 한 페이지의 데이터 개수") @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(gameService.getReleaseGameDTO(page-1,size));
    }

    @GetMapping("/recommend")
    @Operation(summary = "인기게임 조회",description = "인기게임 목록을 조회합니다.")
    public ResponseEntity<Page<GetGameDetailDTO>> getRecommendSortGame(
            @Parameter(description = "페이지 번호") @RequestParam(name = "page", defaultValue = "1") int page,
            @Parameter(description = " 한 페이지의 데이터 개수") @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(gameService.getRecomandGameDTO(page-1,size));
    }

    @GetMapping("/positive")
    @Operation(summary = "압도적으로 긍정적인 게임 목록 조회",description = "스팀의 압도적으로 긍정적인 게임 목록을 조회합니다.")
    public ResponseEntity<Page<GetGameDetailDTO>> getPositiveGame(
            @Parameter(description = "페이지 번호") @RequestParam(name = "page", defaultValue = "1") int page,
            @Parameter(description = " 한 페이지의 데이터 개수") @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(gameService.getPositiveGameDTO(page-1,size));
    }

    @GetMapping("/like")
    @Operation(summary = "사용자가 추천한 게임 목록 조회",description = "사용자가 사이트내에서 추천한 게임 목록을 조회합니다.")
    public ResponseEntity<Page<GetGameDetailDTO>> createArticle(@AuthenticationPrincipal CustomUserDetails userInfo,
                                                                @Parameter(description = "페이지 번호") @RequestParam(name = "page", defaultValue = "1") int page,
                                                                @Parameter(description = " 한 페이지의 데이터 개수") @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(gameService.getLikeGameDTO(page-1,size,userInfo));
    }

    @GetMapping("/category/{id}")
    @Operation(summary = "카테고리 포함된 게임 목록 조회",description = "사용자가 입력한 카테고리에 포함된 게임 목록을 조회합니다.")
    public ResponseEntity<Page<GetGameDetailDTO>> categoryGame(
                                                                @Parameter(required = true, description = "카테고리 일련번호") @PathVariable(name = "id") Long id,
                                                               @Parameter(description = "페이지 번호") @RequestParam(name = "page", defaultValue = "1") int page,
                                                               @Parameter(description = " 한 페이지의 데이터 개수") @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(gameService.getCategoryGameDTO(page - 1, size, id));
    }

    @GetMapping("/category")
    @Operation(summary = "카테고리 목록 조회",description = "서버에 저장된 카테고리 목록을 조회합니다.")
    public ResponseEntity<List<GetGameCategoryDTO>> GetCategoryList() {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getCategoryList());
    }

    // 게임 수 조회
    @GetMapping("/count")
    @Operation(summary = "서버 게임 수 조회",description = "서버에 저장되어 있는 게임 수를 조회합니다.")
    public ResponseEntity<Long> getGameCount() {
        long count = gameService.getGameCount();
        return ResponseEntity.ok(count);
    }

    ///////                       스팀에서 서버로 다운                            ////////

    // 스팀의 전체 게임목록 DB에 다운
    @GetMapping("/request/game/list")
    @Operation(summary = "게임 일련번호 다운",description = "스팀에 있는 전체 게임의 일련번호를 서버로 다운받습니다.")
    public ResponseEntity<String> readSteamGameData() throws JsonProcessingException {
        gameDataService.receiveSteamGameList();
        return ResponseEntity.ok("전체 게임 일련번호 생성완료");
    }

    // 스팀의 전체 상세게임정보 DB에서 다운
    @GetMapping("/request/game/detail")
    @Operation(summary = "전체 게임 상세정보 다운",description = "스팀에 있는 전체 게임의 상세정보를 서버로 다운받습니다.")
    public ResponseEntity<String> saveDetailSteamGameData(
            @Parameter(description = "한 시퀀스에서 서버에 저장 할 게임 수") @RequestParam(name = "size", defaultValue = "10") int size,
            @Parameter(description = "이전 시퀀스부터 이어서 다운 여부") @RequestParam(name = "isContinue", defaultValue = "false") Boolean isContinue
    ) throws JsonProcessingException, InterruptedException {
        gameDataScheduler.startManualDown(isContinue,size);
        return ResponseEntity.ok("전체 게임 상세정보 다운 완료");
    }

    // 스팀의 게임 하나 다운
    @GetMapping("/request/game/{id}")
    @Operation(summary = "게임 상세정보 다운",description = "스팀에 있는 하나의 게임 상세정보를 서버로 다운받습니다.")
    public ResponseEntity<String> readSteamGameDataOne(@PathVariable(name = "id") int id) {
        try {
            gameDataService.gameDetail(id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("게임 상세정보 다운 완료");
    }

    // 스팀의 TOP100 목록 다운
    @GetMapping("/request/game/top")
    @Operation(summary = "TOP100 게임 상세정보 다운",description = "스팀에 있는 TOP100 게임의 상세정보를 서버로 다운받습니다.")
    public ResponseEntity<String> readTopSteamGameData() {
        try {
            gameDataService.receiveTopSteamGameList(100,"read");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("TOP100 상세정보 다운 완료");
    }

    // 상세게임 페이지 로딩시 현재 플레이어 수
    @GetMapping("/request/players/{id}")
    @Operation(summary = "게임의 사용자 수 조회",description = "게임을 이용하고 있는 사용자 수를 조회합니다.")
    public ResponseEntity<GetGamePlayerNumberDTO> readSteamCategoryData(
            @Parameter(required = true,description = "게임 일련번호") @PathVariable(name = "id") String id) throws JsonProcessingException {

        return ResponseEntity.ok(gameDataService.getGamePlayerNumber(id));
    }

//    // json 파일의 카테고리 리스트를 db에 저장
//    @GetMapping("/request/category")
//    public ResponseEntity<Any> readSteamCategoryData() {
//        gameDataService.readCategoryList();
//        return ResponseEntity.ok(null);
//    }

}
