package mytrophy.api.game.repository;

import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.GameData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameDataRepository extends JpaRepository<GameData,Long> {
    GameData findByAppId(Integer appId);
}
