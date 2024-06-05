package mytrophy.api.game.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.QGame;
import mytrophy.api.game.enumentity.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class GameQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QGame qGame = QGame.game;

    public Page<Game> searchGame(
            String keyword, List<Long> categoryIds,
            Integer minPrice, Integer maxPrice,
            boolean isFree, LocalDate startDate,
            LocalDate endDate, Pageable pageable) {

        // 검색 조건 생성
        BooleanExpression predicate = buildSearchPredicate(keyword, categoryIds, minPrice, maxPrice, isFree, startDate, endDate);

        // 정렬 방향 설정
        Sort sort = Sort.unsorted();

        if (pageable.getSort().isSorted()) {
            sort = pageable.getSort();
        } else {
            // 기본적으로 적용할 정렬 방향 지정
            sort = Sort.by(Sort.Direction.DESC, "id"); // 예시로 ID를 기본적으로 내림차순으로 정렬
        }

        // 쿼리 실행 및 페이징하여 결과 반환
        QueryResults<Game> results = jpaQueryFactory
                .selectFrom(qGame)
                .leftJoin(qGame.gameCategoryList).fetchJoin()
                .where(predicate)
                .orderBy(getOrderSpecifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<Game> gameList(Pageable pageable) {

        // 정렬 방향 설정
        Sort sort = pageable.getSort();

        // 쿼리 실행 및 페이징하여 결과 반환
        QueryResults<Game> results = jpaQueryFactory
                .selectFrom(qGame)
                .orderBy(getOrderSpecifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public Page<Game> gamePositiveList(Pageable pageable) {

        // 정렬 방향 설정
        Sort sort = pageable.getSort();

        // 쿼리 실행 및 페이징하여 결과 반환
        QueryResults<Game> results = jpaQueryFactory
                .selectFrom(qGame)
                .where(qGame.positive.eq(Positive.VERY_POSITIVE))
                .orderBy(getOrderSpecifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    private BooleanExpression buildSearchPredicate(
            String keyword, List<Long> categoryIds, Integer minPrice, Integer maxPrice,
            boolean isFree, LocalDate startDate, LocalDate endDate) {
        BooleanExpression predicate = qGame.name.contains(keyword != null ? keyword : "");

        if (categoryIds != null && !categoryIds.isEmpty()) {
            for (Long categoryId : categoryIds) {
                predicate = predicate.and(qGame.gameCategoryList.any().category.id.eq(categoryId));
            }
        }

        if (minPrice != null && maxPrice != null) {
            predicate = predicate.and(qGame.price.between(minPrice, maxPrice));
        } else if (minPrice != null) {
            predicate = predicate.and(qGame.price.goe(minPrice));
        } else if (maxPrice != null) {
            predicate = predicate.and(qGame.price.loe(maxPrice));
        }

        if (isFree) {
            predicate = predicate.and(qGame.price.eq(0));
        }

        if (startDate != null && endDate != null) {
            predicate = predicate.and(qGame.releaseDate.between(startDate, endDate.plusDays(1)));
        }

        return predicate;
    }

    private OrderSpecifier<?>[] getOrderSpecifier(Sort sort) {
        return sort.stream()
                .map(order -> {
                    if (order.isAscending()) {
                        if (order.getProperty().equals("name")) {
                            return qGame.name.stringValue().coalesce("").asc().nullsLast(); // name 필드에 대한 오름차순 정렬 방법 설정
                        } else if (order.getProperty().equals("price")) {
                            return qGame.price.asc().nullsLast(); // price 필드에 대한 오름차순 정렬 방법 설정
                        } else if (order.getProperty().equals("recommendation")) {
                            return qGame.recommendation.asc().nullsLast(); // recommendation 필드에 대한 오름차순 정렬 방법 설정
                        } else if (order.getProperty().equals("releaseDate")) {
                            return qGame.releaseDate.asc().nullsLast(); // releaseDate 필드에 대한 오름차순 정렬 방법 설정
                        }
                    } else {
                        if (order.getProperty().equals("name")) {
                            return qGame.name.stringValue().coalesce("").desc().nullsLast(); // name 필드에 대한 내림차순 정렬 방법 설정
                        } else if (order.getProperty().equals("price")) {
                            return qGame.price.desc().nullsLast(); // price 필드에 대한 내림차순 정렬 방법 설정
                        } else if (order.getProperty().equals("recommendation")) {
                            return qGame.recommendation.desc().nullsLast(); // recommendation 필드에 대한 내림차순 정렬 방법 설정
                        } else if (order.getProperty().equals("releaseDate")) {
                            return qGame.releaseDate.desc().nullsLast(); // releaseDate 필드에 대한 내림차순 정렬 방법 설정
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toArray(OrderSpecifier[]::new);
    }
}
