package mytrophy.api.querydsl.service;

import lombok.RequiredArgsConstructor;
import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.entity.Article;
import mytrophy.api.querydsl.repository.ArticleQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ArticleResponseDto findArticleWithCommentsOrderedByLatest(Long articleId){
        Article article = articleQueryRepository.findArticleWithCommentsOrderedByLatest(articleId);
        return ArticleResponseDto.fromEntity(article);
    }

    // memberId로 좋아요 누른 게시글 조회
    @Transactional(readOnly = true)
    public Page<ArticleResponseDto> getLikedArticlesByMemberId(Long memberId, Pageable pageable) {
        Page<Article> articles = articleQueryRepository.findLikedArticlesByMemberId(memberId, pageable);
        return articles.map(ArticleResponseDto::fromEntity);
    }
}
