package mytrophy.api.game.repository;

import mytrophy.api.game.entity.GameGenres;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GameGenresRepository extends JpaRepository<GameGenres, Long> {
}
