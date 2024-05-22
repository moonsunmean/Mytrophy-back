package mytrophy.api.game.service;

import mytrophy.api.game.repository.GamesRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GamesRepository gamesRepository;

    public GameService(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    public void receiveSteamData() {
    }
}
