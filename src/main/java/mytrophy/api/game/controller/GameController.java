package mytrophy.api.game.controller;

import mytrophy.api.game.service.GameDataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameDataService gameDataService;

    public GameController(GameDataService gameDataService) {
        this.gameDataService = gameDataService;
    }
}
