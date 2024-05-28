package mytrophy.api.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.entity.QArticle;
import mytrophy.api.comment.entity.QComment;
import org.springframework.stereotype.Repository;

import java.util.List;

import static mytrophy.api.article.entity.QArticle.article;
import static mytrophy.api.comment.entity.QComment.comment;

@Repository
public class ArticleQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QArticle qArticle = QArticle.article;
    private final QComment qComment = QComment.comment;

    public ArticleQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<Article> findAllWithCommentsOrderedByLatest() {
        return jpaQueryFactory
            .selectFrom(qArticle)
            .leftJoin(qComment).fetchJoin()
            .orderBy(qComment.createdAt.desc()) // 댓글 작성일자를 기준으로 내림차순 정렬
            .fetch();
    }

}
