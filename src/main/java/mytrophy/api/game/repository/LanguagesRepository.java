package mytrophy.api.game.repository;

import mytrophy.api.game.entity.Languages;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LanguagesRepository extends JpaRepository<Languages, Long> {
}
