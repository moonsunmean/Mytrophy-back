package mytrophy.api.game.repository;

import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameCategoryRepository extends JpaRepository<GameCategory, Long> {
    boolean existsByGameIdAndCategoryId(Long gameId, Long categoryId);

    List<GameCategory> findAllByGame(Game game);
}
