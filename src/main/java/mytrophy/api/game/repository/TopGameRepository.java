package mytrophy.api.game.repository;

import mytrophy.api.game.entity.TopGameRead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopGameRepository extends JpaRepository<TopGameRead,Long> {
}
