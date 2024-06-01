package mytrophy.api.game.repository;

import mytrophy.api.game.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Page<Game> findGameByNameContaining(String keyword, Pageable pageable);


    @Query("SELECT distinct g FROM Game g inner join GameCategory gc ON gc.game = g WHERE gc.category.id = :categoryId AND g.name LIKE CONCAT('%', :keyword, '%')")
    Page<Game> findGameByNameContainingByCategoryId(@Param("keyword") String keyword, Pageable pageable, @Param("categoryId") Long categoryId);

    Game findByAppId(Integer appId);

    boolean existsByAppId(Integer id);
}

//import com.querydsl.core.types.dsl.StringExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import static mytrophy.api.game.entity.QGame.game;
//import static mytrophy.api.game.entity.QGameCategory.gameCategory;
//
//public interface GameRepository extends JpaRepository<Game, Long> {
//
//    Page<Game> findGameByNameContaining(String keyword, Pageable pageable);
//
//    default Page<Game> findGameByNameContainingByCategoryId(String keyword, Pageable pageable, Long categoryId) {
//        JPAQueryFactory queryFactory = new JPAQueryFactory(getEntityManager());
//
//        return QuerydslUtils.applyPagination(pageable, query -> query
//                .selectDistinct(game)
//                .from(game)
//                .join(game.gameCategories, gameCategory)
//                .where(
//                        gameCategory.category.id.eq(categoryId)
//                                .and(game.name.likeIgnoreCase("%" + keyword + "%"))
//                )
//        );
//    }
//
//    Game findByAppId(Integer appId);
//
//    boolean existsByAppId(Integer id);
//}

