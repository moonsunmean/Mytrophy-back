package mytrophy.api.game.repository;

import mytrophy.api.game.entity.GameDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameDetailsRepository extends JpaRepository<GameDetails, Long> {
}
