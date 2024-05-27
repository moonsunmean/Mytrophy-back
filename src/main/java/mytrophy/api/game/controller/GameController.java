package mytrophy.api.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import mytrophy.api.game.dto.ResponseDTO.GetAllGameDTO;
import mytrophy.api.game.dto.ResponseDTO.GetTopGameDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameDetailDTO;
import mytrophy.api.game.dto.ResponseDTO.GetSearchGameDTO;
import mytrophy.api.game.service.GameDataService;
import mytrophy.api.game.service.GameService;
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

    @Autowired
    public GameController(GameService gameService, GameDataService gameDataService) {
        this.gameService = gameService;
        this.gameDataService = gameDataService;
    }


    @GetMapping
    public ResponseEntity<Page<GetAllGameDTO>> getAllGame(@RequestParam(name = "page", defaultValue = "1") int page,
                                                          @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getAllGameDTO(page - 1, size));
    }


    @GetMapping("/{id}")
    public ResponseEntity<GetGameDetailDTO> getGameDetail(@PathVariable(name = "id") Long id) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getGameDetailDTO(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<GetSearchGameDTO>> getSearchGame(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                                @RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
                                                                @RequestParam(name = "page", defaultValue = "1") int page,
                                                                @RequestParam(name = "size", defaultValue = "10") int size
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getSearchGameDTO(keyword, page - 1, size, categoryId));
    }

    @GetMapping("/top100")
    public ResponseEntity<Page<GetTopGameDTO>> getTopGame(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) throws JsonProcessingException {

        return ResponseEntity.status(HttpStatus.OK).body(gameService.getTopGameDTO(page-1,size,gameDataService.receiveTopSteamGameList(100,"request")));
    }



    // 스팀에서 서버로 다운 /////////////////////////////////////////////////////////////////////////////////////

    // 스팀의 전체 게임목록 다운
    @PostMapping("/read/game")
    public ResponseEntity<Any> readSteamGameData(@RequestParam(name = "size", defaultValue = "10") int size) {

        try {
            gameDataService.receiveSteamGameList(size);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(null);
    }

    // 스팀의 게임 하나 다운
    @PostMapping("/read/game/{id}")
    public ResponseEntity<Any> readSteamGameDataOne(@PathVariable(name = "id") int id) {
        try {
            gameDataService.gameDetail(id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(null);
    }

    // 스팀의 TOP100 목록 다운
    @PostMapping("/read/game/top")
    public ResponseEntity<Any> readTopSteamGameData() {
        try {
            gameDataService.receiveTopSteamGameList(100,"read");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/read/category")
    public ResponseEntity<Any> readSteamCategoryData() {
        gameDataService.readCategoryList();
        return ResponseEntity.ok(null);
    }

}
