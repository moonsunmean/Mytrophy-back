package mytrophy.api.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import mytrophy.api.game.dto.tsetDTO;
import mytrophy.api.game.dto.RequestDTO.SearchGameRequestDTO;
import mytrophy.api.game.dto.ResponseDTO.GetAllGameDTO;
import mytrophy.api.game.dto.ResponseDTO.GetTopGameDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameDetailDTO;
import mytrophy.api.game.dto.ResponseDTO.GetSearchGameDTO;
import mytrophy.api.game.service.GameDataService;
import mytrophy.api.game.service.GameService;
import mytrophy.global.scheduler.GameDataScheduler;
import org.hibernate.mapping.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;
    private final GameDataService gameDataService;
    private final GameDataScheduler gameDataScheduler;

    @Autowired
    public GameController(GameService gameService, GameDataService gameDataService, GameDataScheduler gameDataScheduler) {
        this.gameService = gameService;
        this.gameDataService = gameDataService;
        this.gameDataScheduler = gameDataScheduler;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(@RequestBody tsetDTO test) {
        String asd = test.getTest1() + test.getTest2();
        return ResponseEntity.ok(asd);
    }


    @GetMapping
    public ResponseEntity<Page<GetAllGameDTO>> getAllGame(@RequestParam(name = "page", defaultValue = "1") int page,
                                                          @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getAllGameDTO(page - 1, size));
    }


    @GetMapping("/{id}")
    public ResponseEntity<GetGameDetailDTO> getGameDetail(@PathVariable(name = "id") Integer id) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getGameDetailDTO(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<GetSearchGameDTO>> getSearchGame(@RequestBody SearchGameRequestDTO searchGameRequestDTO) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getSearchGameDTO(searchGameRequestDTO));
    }

    @GetMapping("/top100")
    public ResponseEntity<Page<GetTopGameDTO>> getTopGame(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) throws JsonProcessingException {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getTopGameDTO(page-1,size,gameDataService.receiveTopSteamGameList(100,"request")));
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

    // json 파일의 카테고리 리스트를 db에 저장
    @GetMapping("/request/category")
    public ResponseEntity<Any> readSteamCategoryData() {
        gameDataService.readCategoryList();
        return ResponseEntity.ok(null);
    }

}
