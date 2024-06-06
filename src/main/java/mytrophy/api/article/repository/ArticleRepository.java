package mytrophy.api.article.repository;

import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findAll(Pageable pageable);

    Page<Article> findAllByHeader(Header header, Pageable pageable);

    Article findByIdAndHeader(Long id, Header header);

    Page<Article> findByAppId(int appId, Pageable pageable);
}
