package mytrophy.api.querydsl.service;

import mytrophy.api.article.entity.Article;

import java.util.List;

public interface ArticleQueryService {

    List<Article> findAll();

    List<Article> findArticleWithCommentsOrderedByLatest(Long articleId);

}
