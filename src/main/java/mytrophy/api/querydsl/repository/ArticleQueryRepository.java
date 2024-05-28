package mytrophy.api.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.entity.QArticle;
import mytrophy.api.comment.entity.QComment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ArticleQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QArticle qArticle = QArticle.article;
    private final QComment qComment = QComment.comment;

    public ArticleQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<Article> findArticleWithCommentsOrderedByLatest(Long articleId) {
        return jpaQueryFactory
            .selectFrom(qArticle)
            .leftJoin(qArticle.comments, qComment).fetchJoin()
            .where(qArticle.id.eq(articleId))
            .orderBy(qComment.createdAt.desc())
            .fetch();
    }


}
