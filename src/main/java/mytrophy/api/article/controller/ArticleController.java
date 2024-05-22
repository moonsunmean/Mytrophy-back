package mytrophy.api.article.controller;

import lombok.RequiredArgsConstructor;
import mytrophy.api.article.domain.Article;
import mytrophy.api.article.domain.Header;
import mytrophy.api.article.dto.ArticleRequest;
import mytrophy.api.article.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor // final 필드를 파라미터로 받는 생성자를 생성
public class ArticleController {

    private final ArticleService articleService;

    // 게시글 생성
    @PostMapping("/articles")
    public ResponseEntity<?> createArticle(@RequestBody ArticleRequest articleRequest) { // RequestBody:HTTP 요청의 본문을 자바 객체로 전달받을 때 사용
        // 게시글 생성 로직 수행
        Article article = articleService.createArticle(articleRequest.getHeader(), articleRequest.getName(), articleRequest.getContent());

        // ResponseEntity 객체를 통해 HTTP 응답 반환
        return ResponseEntity.ok().body(Collections.singletonMap("id", article.getId())); // singletonMap:단일 키-값 쌍을 가지는 맵을 생성
    }

    // 게시글 리스트 조회
    @GetMapping("/articles")
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok().body(articleService.findAll());
    }

    // 해당 게시글 조회
    @GetMapping("/articles/{id}")
    public ResponseEntity getArticleById(@PathVariable Long id) { // PathVariable:URL 경로에 있는 값을 파라미터로 받을 때 사용
        return ResponseEntity.ok().body(articleService.findById(id));
    }

    // 말머리 별 게시글 리스트 조회
    @GetMapping("/header/{header}/articles")
    public ResponseEntity<List<Article>> getArticlesByHeader(@PathVariable Header header) {
        List<Article> articles;
        // 헤더가 유효한지 검사
        switch (header) {
            case FREE_BOARD:
            case INFORMATION:
            case GUIDE:
            case REVIEW:
            case CHATING:
                // 유효한 헤더인 경우 해당 헤더로 게시글 조회
                articles = articleService.findAllByHeader(header);
                return ResponseEntity.ok().body(articles);
            default:
                // 잘못된 헤더인 경우 예외 처리
                return ResponseEntity.badRequest().build();
        }
    }

    // 말머리 별 해당 게시글 조회
    @GetMapping("/header/{header}/articles/{id}")
    public ResponseEntity getArticleByHeaderAndId(@PathVariable Long id, @PathVariable Header header) {
        return ResponseEntity.ok().body(articleService.findByIdAndHeader(id, header));
    }

    // 게시글 수정
    @PatchMapping("/articles/{id}")
    public ResponseEntity updateArticle(@PathVariable Long id, @RequestBody ArticleRequest articleRequest) {
        Article article = articleService.findById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        articleService.updateArticle(id, articleRequest.getHeader(), articleRequest.getName(), articleRequest.getContent());

        return ResponseEntity.ok().body(Collections.singletonMap("id", id));
    }

    // 게시글 삭제
    @DeleteMapping("/articles/{id}")
    public ResponseEntity deleteArticle(@PathVariable Long id) {
        Article article = articleService.findById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }
}
