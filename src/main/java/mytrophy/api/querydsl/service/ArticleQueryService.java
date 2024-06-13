package mytrophy.api.querydsl.service;

import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleQueryService {

    // 모든 게시글 조회
    List<ArticleResponseDto> findAll();

    ArticleResponseDto findArticleWithCommentsOrderedByLatest(Long articleId);

    // 회원 id로 좋아요한 게시글 id 조회
    Page<ArticleResponseDto> getLikedArticlesByMemberId(Long memberId, Pageable pageable);
}
