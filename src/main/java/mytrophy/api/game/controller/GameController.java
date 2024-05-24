package mytrophy.api.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import mytrophy.api.game.dto.ResponseDTO.GetGameAchievementDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameScreenshotDTO;
import mytrophy.api.game.dto.ResponseDTO.GetAllGameDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameCategoryDTO;
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

    ///////////////////////////////////////////////////////////////////////
    @PostMapping("/read/game")
    public ResponseEntity<Any> readSteamGameData(@RequestParam(name = "size") int size) {

        try {
            gameDataService.receiveSteamGameList(0,size);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/read/game/{id}")
    public ResponseEntity<Any> readSteamGameDataOne(@PathVariable(name = "id") int id) {
        try {
            gameDataService.receiveSteamGameList(id,1);
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
