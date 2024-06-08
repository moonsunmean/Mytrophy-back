package mytrophy.api.article.service;



import mytrophy.api.article.dto.ArticleRequestDto;
import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.enumentity.Header;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface ArticleService {

    // 게시글 생성
    ArticleResponseDto createArticle(Long memberId, ArticleRequestDto articleRequestDto, List<String> imagePath) throws IOException;

    // 게시글 리스트 조회
    Page<ArticleResponseDto> findAll(Pageable pageable);

    // 해당 게시글 조회
    ArticleResponseDto findById(Long id);

    // 말머리 별 게시글 리스트 조회
    Page<ArticleResponseDto> findAllByHeader(Header header, Pageable pageable);

    // 말머리 별 해당 게시글 조회
    ArticleResponseDto findByIdAndHeader(Long id, Header header);

    // 게시글 수 조회
    long getArticleCount();

    // 게시글 수정
    ArticleResponseDto updateArticle(Long memberId, Long id, ArticleRequestDto articleRequestDto);

    // 게시글 삭제
    void deleteArticle(Long id);

    // 유저 권한 확인
    boolean isAuthorized(Long id, Long memberId);

    // 좋아요 확인
    boolean checkLikeArticle(Long articleId, Long memberId);

    // 좋아요 수 증가
    void articleLikeUp(Long articleId, Long memberId);

    void articleLikeDown(Long articleId, Long memberId);

    Page<ArticleResponseDto> findByAppId(int appId, Pageable pageable);
}
