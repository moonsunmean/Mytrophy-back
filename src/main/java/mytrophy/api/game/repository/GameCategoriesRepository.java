package mytrophy.api.game.repository;

import mytrophy.api.game.entity.GameCategories;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GameCategoriesRepository extends JpaRepository<GameCategories, Long> {
}
