package mytrophy.api.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.service.ArticleService;
import mytrophy.api.game.dto.RequestDTO.UpdateGameRequestDTO;
import mytrophy.api.game.dto.RequestDTO.UpdateGameCategoryDTO;
import mytrophy.api.game.dto.RequestDTO.SearchGameRequestDTO;
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
    public ResponseEntity<Page<GetGameDetailDTO>> getAllGame(@RequestParam(name = "page", defaultValue = "1") int page,
                                                          @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getAllGameDTO(page - 1, size));
    }

    @GetMapping("/{appId}")
    public ResponseEntity<GetGameDetailDTO> getGameDetail(@PathVariable(name = "appId") Integer appId) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getGameDetailDTO(appId));
    }

    @PostMapping("/{appId}")
    public ResponseEntity<String> updateGameDetail(@PathVariable(name = "appId") Integer appId,@RequestBody UpdateGameRequestDTO updateGameRequestDTO) {
        if (gameService.updateGameDetail(appId,updateGameRequestDTO)) {
            return ResponseEntity.status(HttpStatus.OK).body("게임정보 수정에 성공했습니다.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게임정보 수정에 실패했습니다.");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<GetGameDetailDTO>> getSearchGame(@RequestBody SearchGameRequestDTO searchGameRequestDTO) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getSearchGameDTO(searchGameRequestDTO));
    }

    @GetMapping("/top100")
    public ResponseEntity<Page<GetTopGameDTO>> getTopGame(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) throws JsonProcessingException {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getTopGameDTO(page-1,size,gameDataService.receiveTopSteamGameList(100,"request")));
    }

    @GetMapping("/release")
    public ResponseEntity<Page<GetGameDetailDTO>> getReleaseSortGame(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getReleaseGameDTO(page-1,size));
    }

    @GetMapping("/recommend")
    public ResponseEntity<Page<GetGameDetailDTO>> getRecommendSortGame(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getRecomandGameDTO(page-1,size));
    }

    @GetMapping("/positive")
    public ResponseEntity<Page<GetGameDetailDTO>> getPositiveGame(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getPositiveGameDTO(page-1,size));
    }

    @GetMapping("/like")
    public ResponseEntity<Page<GetGameDetailDTO>> createArticle(@AuthenticationPrincipal CustomUserDetails userInfo,
                                                                @RequestParam(name = "page", defaultValue = "1") int page,
                                                                @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getLikeGameDTO(page-1,size,userInfo));
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Page<GetGameDetailDTO>> categoryGame(@PathVariable(name = "id") Long id,
                                                               @RequestParam(name = "page", defaultValue = "1") int page,
                                                               @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getCategoryGameDTO(page - 1, size, id));
    }

    // 게임 수 조회
    @GetMapping("/count")
    public ResponseEntity<Long> getGameCount() {
        long count = gameService.getGameCount();
        return ResponseEntity.ok(count);
    }

    ///////                       스팀에서 서버로 다운                            ////////

    // 스팀의 전체 게임목록 DB에 다운
    @GetMapping("/request/game/list")
    public ResponseEntity<Any> readSteamGameData() throws JsonProcessingException {
        gameDataService.receiveSteamGameList();
        return ResponseEntity.ok(null);
    }

    // 스팀의 전체 상세게임정보 DB에서 다운
    @GetMapping("/request/game/detail")
    public ResponseEntity<Any> saveDetailSteamGameData(
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "isContinue", defaultValue = "false") Boolean isContinue
    ) throws JsonProcessingException, InterruptedException {
        gameDataScheduler.startManualDown(isContinue,size);
        return ResponseEntity.ok(null);
    }

    // 스팀의 게임 하나 다운
    @GetMapping("/request/game/{id}")
    public ResponseEntity<Any> readSteamGameDataOne(@PathVariable(name = "id") int id) {
        try {
            gameDataService.gameDetail(id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(null);
    }

    // 스팀의 TOP100 목록 다운
    @GetMapping("/request/game/top")
    public ResponseEntity<Any> readTopSteamGameData() {
        try {
            gameDataService.receiveTopSteamGameList(100,"read");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(null);
    }

    // 상세게임 페이지 로딩시 현재 플레이어 수
    @GetMapping("/request/players/{id}")
    public ResponseEntity<GetGamePlayerNumberDTO> readSteamCategoryData(@PathVariable(name = "id") String id) throws JsonProcessingException {

        return ResponseEntity.ok(gameDataService.getGamePlayerNumber(id));
    }

    // json 파일의 카테고리 리스트를 db에 저장
    @GetMapping("/request/category")
    public ResponseEntity<Any> readSteamCategoryData() {
        gameDataService.readCategoryList();
        return ResponseEntity.ok(null);
    }

}
