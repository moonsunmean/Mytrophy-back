package mytrophy.api.article.service;



import mytrophy.api.article.dto.ArticleRequestDto;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;

import java.io.IOException;
import java.util.List;

public interface ArticleService {

    // 게시글 생성
    Article createArticle(Long memberId, ArticleRequestDto articleRequestDto, List<String> imagePath) throws IOException;

    // 게시글 리스트 조회
    List<Article> findAll();

    // 해당 게시글 조회
    Article findById(Long id);

    // 말머리 별 게시글 리스트 조회
    List<Article> findAllByHeader(Header header);

    // 말머리 별 해당 게시글 조회
    Article findByIdAndHeader(Long id, Header header);

    // 게시글 수정
    Article updateArticle(Long memberId, Long id, ArticleRequestDto articleRequestDto);

    // 게시글 삭제
    void deleteArticle(Long memberId, Long id);

    // 유저 권한 확인
    boolean isAuthorized(Long id, Long memberId);

    // 좋아요 증가
    void upCntUp(Long id);

    // 좋아요 감소
    void CntUpDown(Long id);

}
