package mytrophy.api.game.repository;

import mytrophy.api.game.entity.GameRead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameReadRepository extends JpaRepository<GameRead,Long> {
}
