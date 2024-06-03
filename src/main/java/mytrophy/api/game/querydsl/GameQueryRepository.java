package mytrophy.api.game.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mytrophy.api.game.dto.ResponseDTO.GetSearchGameDTO;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.QGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QGame qGame = QGame.game;

    /**
     * 게임을 검색하는 메서드입니다.
     *
     * @param keyword                       검색 키워드
     * @param categoryId                    카테고리 ID
     * @param minPrice                      최소 가격
     * @param maxPrice                      최대 가격
     * @param isFree                        무료 여부
     * @param startDate                     시작 날짜
     * @param endDate                       종료 날짜
     * @param pageable                      페이지 정보
     * @return 검색된 게임 페이지
     */
    public Page<Game> searchGame(
            String keyword, Long categoryId,
            Integer minPrice, Integer maxPrice,
            boolean isFree, LocalDate startDate,
            LocalDate endDate, Pageable pageable) {

        // 검색 조건 생성
        BooleanExpression predicate = buildSearchPredicate(keyword, categoryId, minPrice, maxPrice, isFree, startDate, endDate);

        // 쿼리 실행 및 페이징하여 결과 반환
        QueryResults<Game> results = jpaQueryFactory
                .selectFrom(qGame)
                .leftJoin(qGame.gameCategoryList).fetchJoin()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    /**
     * 검색 조건을 생성하는 메서드입니다.
     *
     * @param keyword                       검색 키워드
     * @param categoryId                    카테고리 ID
     * @param minPrice                      최소 가격
     * @param maxPrice                      최대 가격
     * @param isFree                        무료 여부
     * @param startDate                     시작 날짜
     * @param endDate                       종료 날짜
     * @return 생성된 검색 조건
     */
    private BooleanExpression buildSearchPredicate(
            String keyword, Long categoryId, Integer minPrice, Integer maxPrice,
            boolean isFree, LocalDate startDate, LocalDate endDate) {
        BooleanExpression predicate = qGame.name.contains(keyword != null ? keyword : "");

        if (categoryId != null) {
            predicate = predicate.and(qGame.gameCategoryList.any().category.id.eq(categoryId));
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

    /**
     * 이름 정렬 표현식을 반환하는 메서드입니다.
     *
     * @param direction 이름 정렬 방향
     * @return 이름 정렬 표현식
     */
    private OrderSpecifier<String> getNameSortExpression(Sort.Direction direction) {
        if (direction == null) {
            return null;
        }
        return direction.isAscending() ? qGame.name.asc() : qGame.name.desc();
    }

    /**
     * 가격 정렬 표현식을 반환하는 메서드입니다.
     *
     * @param direction 가격 정렬 방향
     * @return 가격 정렬 표현식
     */
    private OrderSpecifier<Integer> getPriceSortExpression(Sort.Direction direction) {
        if (direction == null) {
            return null;
        }
        return direction.isAscending() ? qGame.price.asc() : qGame.price.desc();
    }

    /**
     * 추천 수 정렬 표현식을 반환하는 메서드입니다.
     *
     * @param direction 추천 수 정렬 방향
     * @return 추천 수 정렬 표현식
     */
    private OrderSpecifier<Integer> getRecommendationSortExpression(Sort.Direction direction) {
        if (direction == null) {
            return null;
        }
        return direction.isAscending() ? qGame.recommendation.asc() : qGame.recommendation.desc();
    }

    /**
     * 날짜 정렬 표현식을 반환하는 메서드입니다.
     *
     * @param direction 날짜 정렬 방향
     * @return 날짜 정렬 표현식
     */
    private OrderSpecifier<LocalDate> getDateSortExpression(Sort.Direction direction) {
        if (direction == null) {
            return null;
        }
        return direction.isAscending() ? qGame.releaseDate.asc() : qGame.releaseDate.desc();
    }
}
