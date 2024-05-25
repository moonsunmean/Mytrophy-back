package mytrophy.api.game.repository;

import mytrophy.api.game.entity.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GameCategoryRepository extends JpaRepository<GameCategory, Long> {
    boolean existsByGameIdAndCategoryId(Long gameId, Long categoryId);
}
