package mytrophy.api.game.repository;

import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.game.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    Page<Game> findGameByNameContaining(String keyword, Pageable pageable);


    @Query("SELECT distinct g FROM Game g inner join GameCategory gc ON gc.game = g WHERE gc.category.id = :categoryId AND g.name LIKE CONCAT('%', :keyword, '%')")
    Page<Game> findGameByNameContainingByCategoryId(@Param("keyword") String keyword, Pageable pageable, @Param("categoryId") Long categoryId);

    Game findByAppId(Integer appId);

    Boolean existsByAppId(Integer id);

    Integer deleteByAppId(Integer appId);

    @Query("SELECT g FROM Game g " +
            "JOIN g.gameCategoryList gc " +
            "WHERE gc.category.id IN :categoryIds " +
            "AND g.id NOT IN (SELECT gr.game.id FROM GameReview gr WHERE gr.member.id = :memberId AND (gr.reviewStatus = 'BAD' OR gr.reviewStatus = 'NORMAL')) " +
            "GROUP BY g.id " +
            "ORDER BY COUNT(DISTINCT gc.category.id) DESC, g.recommendation DESC")
    Page<Game> findRecommendedGames(@Param("categoryIds") List<Long> categoryIds, @Param("memberId") Long memberId, Pageable pageable);
}


