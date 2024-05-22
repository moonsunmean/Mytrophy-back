package mytrophy.api.game.repository;

import mytrophy.api.game.entity.Achievements;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementsRepository extends JpaRepository<Achievements, Long> {
}
