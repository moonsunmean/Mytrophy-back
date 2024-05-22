package mytrophy.api.game.repository;

import mytrophy.api.game.entity.Games;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GamesRepository extends JpaRepository<Games, Long> {
}
