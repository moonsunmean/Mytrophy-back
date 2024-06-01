package mytrophy.api.querydsl.service;

import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.entity.Article;

import java.util.List;

public interface ArticleQueryService {

    // 모든 게시글 조회
    List<ArticleResponseDto> findAll();

    // 해당 게시글 조회 시 댓글 작성일자를 기준으로 내림차순 정렬
//    List<ArticleResponseDto> findArticleWithCommentsOrderedByLatest(Long articleId);

    ArticleResponseDto findArticleWithCommentsOrderedByLatest(Long articleId);
}
