package mytrophy.api.article.service;

import mytrophy.api.article.entity.Article;
import mytrophy.api.article.entity.Header;
import mytrophy.api.article.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ArticleServiceTest {

    ArticleRepository articleRepository = Mockito.mock(ArticleRepository.class);
    ArticleService articleService = new ArticleService(articleRepository);

    @Test
    @DisplayName("게시글 생성 테스트")
    void createArticle() {
        // given
        Header header = Header.FREE_BOARD;
        String name = "게시글 제목";
        String content = "게시글 내용";

        // when
        articleService.createArticle(header, name, content);

        // then
        Mockito.verify(articleRepository, Mockito.times(1)).save(Mockito.any());

    }

    @Test
    @DisplayName("게시글 리스트 조회 테스트")
    void findAll() {
        // given
        articleService.createArticle(Header.FREE_BOARD, "자유 제목", "자유 내용");
        articleService.createArticle(Header.INFORMATION, "정보 제목", "정보 내용");
        articleService.createArticle(Header.GUIDE, "공략 제목", "공략 내용");
        articleService.createArticle(Header.REVIEW, "리뷰 제목", "리뷰 내용");
        articleService.createArticle(Header.CHATING, "채팅 제목", "채팅 내용");

        List<Article> mockArticles = List.of(
            new Article(1L, Header.FREE_BOARD, "자유 제목", "자유 내용", 0),
            new Article(2L, Header.INFORMATION, "정보 제목", "정보 내용", 0),
            new Article(3L, Header.GUIDE, "공략 제목", "공략 내용", 0),
            new Article(4L, Header.REVIEW, "리뷰 제목", "리뷰 내용", 0),
            new Article(5L, Header.CHATING, "채팅 제목", "채팅 내용", 0)
        );
        when(articleRepository.findAll()).thenReturn(mockArticles);

        // when
        List<Article> articles = articleService.findAll();

        // then
        assertEquals(mockArticles.size(), articles.size());
        Mockito.verify(articleRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("해당 게시글 조회 테스트")
    void findById() {
        // given
        Article article = new Article(1L, Header.FREE_BOARD, "자유 제목", "자유 내용", 0);
        when(articleRepository.findById(1L)).thenReturn(java.util.Optional.of(article));

        // when
        Article findArticle = articleService.findById(1L);

        // then
        assertEquals(article, findArticle);
        Mockito.verify(articleRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void updateArticle() {
        // given
        Article article = new Article(1L, Header.FREE_BOARD, "자유 제목", "자유 내용", 0);
        when(articleRepository.findById(1L)).thenReturn(java.util.Optional.of(article));

        // when
        Article updateArticle = articleService.updateArticle(1L, Header.INFORMATION, "정보 수정된 제목", "정보 수정된 내용");

        // then
        assertEquals(Header.INFORMATION, updateArticle.getHeader());
        assertEquals("정보 수정된 제목", updateArticle.getName());
        assertEquals("정보 수정된 내용", updateArticle.getContent());
        Mockito.verify(articleRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deleteArticle() {
        // given
        Article article = new Article(1L, Header.FREE_BOARD, "자유 제목", "자유 내용", 0);
        when(articleRepository.findById(1L)).thenReturn(java.util.Optional.of(article));

        // when
        articleService.deleteArticle(1L);

        // then
        Mockito.verify(articleRepository, Mockito.times(1)).delete(article);
        List<Article> findAll = articleService.findAll();
        assertEquals(0, findAll.size());
    }

    @Test
    @DisplayName("좋아요 증가 테스트")
    void upCntUp() {
        // given
        Article article = new Article(1L, Header.FREE_BOARD, "자유 제목", "자유 내용", 0);
        when(articleRepository.findById(1L)).thenReturn(java.util.Optional.of(article));

        // when
        articleService.upCntUp(1L);

        // then
        assertEquals(1, article.getCntUp());
        Mockito.verify(articleRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("말머리 별 게시글 리스트 조회 테스트")
    void findAllByHeader() {
        // given
        articleService.createArticle(Header.FREE_BOARD, "자유 제목", "자유 내용");
        articleService.createArticle(Header.INFORMATION, "정보 제목", "정보 내용");
        articleService.createArticle(Header.GUIDE, "공략 제목", "공략 내용");
        articleService.createArticle(Header.REVIEW, "리뷰 제목", "리뷰 내용");
        articleService.createArticle(Header.CHATING, "채팅 제목", "채팅 내용");

        List<Article> mockArticles = List.of(
            new Article(5L, Header.CHATING, "채팅 제목", "채팅 내용", 0)
        );
        when(articleRepository.findByHeader(Header.CHATING)).thenReturn(mockArticles);

        // when
        List<Article> articles = articleService.findAllByHeader(Header.CHATING);

        // then
        assertEquals(mockArticles.size(), articles.size());
        Mockito.verify(articleRepository, Mockito.times(1)).findByHeader(Header.CHATING);
    }

    // 말머리 별 해당 게시글 조회 테스트
    @Test
    @DisplayName("말머리 별 해당 게시글 조회 테스트")
    void findByIdAndHeader() {
        // given
        articleService.createArticle(Header.FREE_BOARD, "자유 제목", "자유 내용");
        articleService.createArticle(Header.INFORMATION, "정보 제목", "정보 내용");
        articleService.createArticle(Header.GUIDE, "공략 제목", "공략 내용");
        articleService.createArticle(Header.REVIEW, "리뷰 제목", "리뷰 내용");
        articleService.createArticle(Header.CHATING, "채팅 제목", "채팅 내용");

        Article mockArticle = new Article(3L, Header.GUIDE, "공략 제목", "공략 내용", 0);
        when(articleRepository.findByIdAndHeader(1L, Header.GUIDE)).thenReturn(mockArticle);

        // when
        Article article = articleService.findByIdAndHeader(1L, Header.GUIDE);

        //then
        assertEquals(mockArticle, article);
    }

}