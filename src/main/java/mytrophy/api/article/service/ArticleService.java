package mytrophy.api.article.service;



import mytrophy.api.article.dto.ArticleRequestDto;
import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;

import java.io.IOException;
import java.util.List;

public interface ArticleService {

    // 게시글 생성
    ArticleResponseDto createArticle(Long memberId, ArticleRequestDto articleRequestDto, List<String> imagePath) throws IOException;

    // 게시글 리스트 조회
    List<ArticleResponseDto> findAll();

    // 해당 게시글 조회
    ArticleResponseDto findById(Long id);

    // 말머리 별 게시글 리스트 조회
    List<ArticleResponseDto> findAllByHeader(Header header);

    // 말머리 별 해당 게시글 조회
    ArticleResponseDto findByIdAndHeader(Long id, Header header);

    // 게시글 수정
    ArticleResponseDto updateArticle(Long memberId, Long id, ArticleRequestDto articleRequestDto);

    // 게시글 삭제
    void deleteArticle(Long id);

    // 유저 권한 확인
    boolean isAuthorized(Long id, Long memberId);

    // 좋아요 증가
    void upCntUp(Long id);

    // 좋아요 감소
    void CntUpDown(Long id);

}
