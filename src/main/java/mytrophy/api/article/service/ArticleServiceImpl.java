package mytrophy.api.article.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mytrophy.api.article.dto.ArticleRequest;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;
import mytrophy.api.article.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    // 게시글 생성
    @Transactional // 트랜잭션 처리
    public Article createArticle(ArticleRequest articleRequest, List<MultipartFile> files) throws IOException {

        Article article = Article.builder()
            .header(articleRequest.getHeader())
            .name(articleRequest.getName())
            .content(articleRequest.getContent())
            .build();

        return articleRepository.save(article);
    }

    // 게시글 리스트 조회
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    // 해당 게시글 조회
    public Article findById(Long id) {
        return articleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
    }

    // 말머리 별 게시글 리스트 조회
    public List<Article> findAllByHeader(Header header) {
        return articleRepository.findByHeader(header);
    }

    // 말머리 별 해당 게시글 조회
    public Article findByIdAndHeader(Long id, Header header) {
        return articleRepository.findByIdAndHeader(id, header);
    }

    // 게시글 수정
    @Transactional
    public Article updateArticle(Long id, Header header, String name, String content) {
        Article article = findById(id);
        article.update(header, name, content);
        return article;
    }

    // 게시글 삭제
    @Transactional
    public void deleteArticle(Long id) {
        Article article = findById(id);
        articleRepository.delete(article);
    }

    // 좋아요 증가
    @Transactional
    public void upCntUp(Long id) {
        Article article = findById(id);
        article.upCntUp();
    }

    // 좋아요 감소
    @Transactional
    public void CntUpDown(Long id) {
        Article article = findById(id);
        article.CntUpDown();
    }

}
