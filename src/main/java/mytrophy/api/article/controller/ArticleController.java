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
@RequestMapping("api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<?> createArticle(@RequestBody ArticleRequest articleRequest) {
        // 게시글 생성 로직 수행
        Article article = articleService.createArticle(articleRequest.getHeader(), articleRequest.getName(), articleRequest.getContent());

        // 생성된 게시글의 ID를 JSON 응답으로 반환
        return ResponseEntity.ok().body(Collections.singletonMap("id", article.getId()));
    }

    // 게시글 리스트 조회
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok().body(articleService.findAll());
    }

    // 해당 게시글 조회
    @GetMapping("/{id}")
    public String getArticleById(@PathVariable Long id) { // PathVariable:URL 경로에 있는 값을 파라미터로 받을 때 사용
        return "getArticleById";
    }

    // 게시글 수정
    @PatchMapping("/{id}")
    public String updateArticle(@PathVariable Long id) {
        return "updateArticle";
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public String deleteArticle(@PathVariable Long id) {
        return "deleteArticle";
    }
}
