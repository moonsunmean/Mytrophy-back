package mytrophy.api.querydsl.service;

import lombok.RequiredArgsConstructor;
import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.entity.Article;
import mytrophy.api.querydsl.repository.ArticleQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleQueryServiceImpl implements ArticleQueryService{

    private final ArticleQueryRepository articleQueryRepository;

    // 모든 게시글 조회
    public List<ArticleResponseDto> findAll() {
        List<Article> articles = articleQueryRepository.findAll();
        return articles.stream()
            .map(ArticleResponseDto::fromEntity)
            .collect(Collectors.toList());
    }

    // 해당 게시글 조회 시 댓글 작성일자를 기준으로 내림차순 정렬
    public List<ArticleResponseDto> findArticleWithCommentsOrderedByLatest(Long articleId) {
        List<Article> articles = articleQueryRepository.findArticleWithCommentsOrderedByLatest(articleId);
        return articles.stream()
            .map(ArticleResponseDto::fromEntity)
            .collect(Collectors.toList());
    }
}
