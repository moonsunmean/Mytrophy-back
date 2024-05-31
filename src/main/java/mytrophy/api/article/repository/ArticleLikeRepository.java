package mytrophy.api.article.repository;

import mytrophy.api.article.entity.Article;
import mytrophy.api.article.entity.ArticleLike;
import mytrophy.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    Optional<ArticleLike> findByArticleAndMember(Article article, Member member);
}
