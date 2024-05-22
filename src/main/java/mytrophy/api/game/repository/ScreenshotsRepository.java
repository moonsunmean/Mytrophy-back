package mytrophy.api.game.repository;

import mytrophy.api.game.entity.Screenshots;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ScreenshotsRepository extends JpaRepository<Screenshots, Long> {
}
