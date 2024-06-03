package mytrophy.api.game.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.QGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
     * @param recommendation                추천 수
     * @param isFree                        무료 여부
     * @param startDate                     시작 날짜
     * @param endDate                       종료 날짜
     * @param pageable                      페이지 정보
     * @param nameSortDirection             이름 정렬 방향
     * @param priceSortDirection            가격 정렬 방향
     * @param recommendationSortDirection  추천 수 정렬 방향
     * @return 검색된 게임 페이지
     */
    public Page<Game> searchGame(
            String keyword, Long categoryId, Integer minPrice, Integer maxPrice,
            Integer recommendation, boolean isFree, String startDate, String endDate,
            Pageable pageable, Sort.Direction nameSortDirection, Sort.Direction priceSortDirection,
            Sort.Direction recommendationSortDirection) {

        // 검색 조건 생성
        BooleanExpression predicate = buildSearchPredicate(keyword, categoryId, minPrice, maxPrice, recommendation, isFree, startDate, endDate);

        // 페이징된 검색 결과 반환
        QueryResults<Game> results = jpaQueryFactory
                .selectFrom(qGame)
                .leftJoin(qGame.gameCategoryList).fetchJoin()
                .where(predicate)
                .orderBy(getSortExpression(nameSortDirection), getSortExpression(priceSortDirection),
                        getSortExpression(recommendationSortDirection))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Game> resultList = results.getResults();

        long total = results.getTotal();

        return new PageImpl<>(resultList, pageable, total);
    }

    /**
     * 검색 조건을 생성하는 메서드입니다.
     *
     * @param keyword                       검색 키워드
     * @param categoryId                    카테고리 ID
     * @param minPrice                      최소 가격
     * @param maxPrice                      최대 가격
     * @param recommendation                추천 수
     * @param isFree                        무료 여부
     * @param startDate                     시작 날짜
     * @param endDate                       종료 날짜
     * @return 생성된 검색 조건
     */
    private BooleanExpression buildSearchPredicate(
            String keyword, Long categoryId, Integer minPrice, Integer maxPrice,
            Integer recommendation, boolean isFree, String startDate, String endDate) {
        BooleanExpression predicate = qGame.name.contains(keyword);

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

        if (recommendation != null) {
            predicate = predicate.and(qGame.recommendation.eq(recommendation));
        }

        if (isFree) {
            predicate = predicate.and(qGame.price.eq(0));
        }

        if (startDate != null && endDate != null) {
            LocalDate parsedStartDate = parseDate(startDate);
            LocalDate parsedEndDate = parseDate(endDate).plusDays(1);
            predicate = predicate.and(qGame.releaseDate.between(parsedStartDate, parsedEndDate));
        }

        return predicate;
    }

    /**
     * 정렬 방향에 따른 정렬 표현식을 반환하는 메서드입니다.
     *
     * @param direction 정렬 방향
     * @return 정렬 표현식
     */
    private OrderSpecifier<?> getSortExpression(Sort.Direction direction) {
        if (direction == null) {
            return null;
        }
        switch (direction) {
            case ASC:
                return qGame.name.asc();
            case DESC:
                return qGame.name.desc();
            default:
                return null;
        }
    }

    /**
     * 문자열 형식의 날짜를 LocalDate로 변환하는 메서드입니다.
     *
     * @param dateString 날짜 문자열
     * @return LocalDate 객체
     */
    private LocalDate parseDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        return LocalDate.parse(dateString, formatter);
    }
}