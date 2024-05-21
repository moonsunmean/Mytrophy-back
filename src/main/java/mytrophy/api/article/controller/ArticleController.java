package mytrophy.api.article.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/article")
public class ArticleController {

    @GetMapping
    public String getArticle() {
        return "Article";
    }
}
