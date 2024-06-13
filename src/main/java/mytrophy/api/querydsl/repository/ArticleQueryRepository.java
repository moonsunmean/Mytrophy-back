package mytrophy.api.querydsl.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.entity.QArticle;
import mytrophy.api.article.entity.QArticleLike;
import mytrophy.api.comment.entity.Comment;
import mytrophy.api.comment.entity.QComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ArticleQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QArticle qArticle = QArticle.article;
    private final QComment qComment = QComment.comment;
    private final QArticleLike qArticleLike = QArticleLike.articleLike;

    public ArticleQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    // 모든 게시글 조회
    public List<Article> findAll() {
        return jpaQueryFactory
            .selectFrom(qArticle)
            .leftJoin(qArticle.member).fetchJoin()  // Member 엔티티를 Fetch Join하여 함께 로딩
            .fetch();
    }

    //특정 게시글 조회 (전체 댓글과 대댓글 포함)
    public Article findArticleWithCommentsOrderedByLatest(Long articleId) {
        //게시글 조회
        Article article = jpaQueryFactory
                .selectFrom(qArticle)
                .leftJoin(qArticle.member).fetchJoin()
                .where(qArticle.id.eq(articleId))
                .fetchOne();

        if (article == null) {
            return null;
        }

        //댓글, 대댓글 조회
        List<Comment> comments = jpaQueryFactory
                .selectFrom(qComment)
                .leftJoin(qComment.article, qArticle).fetchJoin()
                .leftJoin(qComment.member).fetchJoin()
                .leftJoin(qComment.parentComment).fetchJoin()
                .where(qComment.article.id.eq(articleId))
                .orderBy(
                        qComment.parentComment.id.asc().nullsFirst(), qComment.createdAt.desc()
                )
                .fetch();

        article.setComments(comments);

        return article;
    }

    // 좋아요 누른 게시글 조회
    public Page<Article> findLikedArticlesByMemberId(Long memberId, Pageable pageable) {
        List<Article> articles = jpaQueryFactory
            .selectFrom(qArticle)
            .innerJoin(qArticle.likes, qArticleLike)
            .where(qArticleLike.member.id.eq(memberId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 전체 게시글 수 조회
        long totalCount = jpaQueryFactory
            .selectFrom(qArticle)
            .innerJoin(qArticle.likes, qArticleLike)
            .where(qArticleLike.member.id.eq(memberId))
            .fetchCount();

        return new PageImpl<>(articles, pageable, totalCount);
    }
}
