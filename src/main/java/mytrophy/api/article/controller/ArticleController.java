package mytrophy.api.article.controller;

import lombok.RequiredArgsConstructor;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.entity.Header;
import mytrophy.api.article.dto.ArticleRequest;
import mytrophy.api.article.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor // final 필드를 파라미터로 받는 생성자를 생성
public class ArticleController {

    private final ArticleService articleService;

    // 게시글 생성
    @PostMapping("/articles")
    public ResponseEntity<Article> createArticle(@ModelAttribute ArticleRequest articleRequest,
                                                 @RequestPart (value = "file", required = false) List<MultipartFile> files) throws IOException {
        return ResponseEntity.status(HttpStatus.OK)
            .body(articleService.createArticle(articleRequest, files));

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
        Article article = articleService.findByIdAndHeader(id, header);

        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(article);
    }

    // 게시글 수정
    @PatchMapping("/articles/{id}")
    public ResponseEntity updateArticle(@PathVariable Long id, ArticleRequest articleRequest) {
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
