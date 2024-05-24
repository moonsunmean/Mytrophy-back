package mytrophy.api.game.repository;

import mytrophy.api.game.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Boolean existsByName(String name);
}
