package mytrophy.api.game.repository;

import mytrophy.api.game.entity.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ScreenshotRepository extends JpaRepository<Screenshot, Long> {
}
