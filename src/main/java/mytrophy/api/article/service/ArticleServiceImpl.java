package mytrophy.api.article.service;

import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.entity.ArticleLike;
import mytrophy.api.article.repository.ArticleLikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mytrophy.api.article.dto.ArticleRequestDto;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;
import mytrophy.api.article.repository.ArticleRepository;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.global.handler.resourcenotfound.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional // 트랜잭션 전역 처리
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final ArticleLikeRepository articleLikeRepository;

    // 게시글 생성
    @Override
    public ArticleResponseDto createArticle(Long memberId, ArticleRequestDto articleRequestDto, List<String> imagePath) throws IOException {
        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        if (articleRequestDto.getAppId() == null) {
            throw new IllegalArgumentException("appId가 존재하지 않습니다.");
        }

        // 이미지 경로가 null이 아닌 경우
        Article article = Article.builder()
            .header(articleRequestDto.getHeader())
            .name(articleRequestDto.getName())
            .content(articleRequestDto.getContent())
            .imagePath(imagePath != null && !imagePath.isEmpty() ? String.join(",", imagePath) : null)
            .appId(articleRequestDto.getAppId())
            .member(member)
            .build();

        Article savedArticle = articleRepository.save(article);

        // 생성된 게시글을 ResponseDto로 변환하여 반환
        return ArticleResponseDto.fromEntity(savedArticle);
    }

    // 게시글 리스트 조회
    @Override
    public Page<ArticleResponseDto> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable).map(article -> {
            int commentCount = article.getComments().size();
            return ArticleResponseDto.fromEntityWithCommentCount(article, commentCount);
        });
    }

    // 해당 게시글 조회
    @Override
    public ArticleResponseDto findById(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        return ArticleResponseDto.fromEntity(article);
    }

    // 말머리 별 게시글 리스트 조회
    @Override
    public Page<ArticleResponseDto> findAllByHeader(Header header, Pageable pageable) {
        return articleRepository.findAllByHeader(header, pageable).map(article -> {
            int commentCount = article.getComments().size();
            return ArticleResponseDto.fromEntityWithCommentCount(article, commentCount);
        });
    }

    // 말머리 별 해당 게시글 조회
    @Override
    public ArticleResponseDto findByIdAndHeader(Long id, Header header) {
        Article article = articleRepository.findByIdAndHeader(id, header);
        if (article == null) {
            throw new ResourceNotFoundException("해당 게시글이 존재하지 않습니다.");
        }
        return ArticleResponseDto.fromEntity(article);
    }

    // 게시글 수 조회
    @Override
    public long getArticleCount() {
        return articleRepository.count();
    }

    // 게시글 수정
    @Override
    public ArticleResponseDto updateArticle(Long memberId, Long id, ArticleRequestDto articleRequestDto) {
        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 게시글 조회
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게시글이 존재하지 않습니다."));

        // 게시글 정보 업데이트
        article.updateArticle(articleRequestDto.getHeader(), articleRequestDto.getName(), articleRequestDto.getContent(), articleRequestDto.getImagePath(), articleRequestDto.getAppId());

        // 엔티티를 저장하고, 저장된 엔티티를 기반으로 DTO 객체 생성하여 반환
        return ArticleResponseDto.fromEntity(articleRepository.save(article));
    }


    // 게시글 삭제
    @Override
    public void deleteArticle(Long id) {
        // 게시글 정보 가져오기
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게시글이 존재하지 않습니다."));

        articleRepository.deleteById(article.getId());
    }

    // 유저 권한 확인
    public boolean isAuthorized(Long id, Long memberId) {
        // 게시글 정보 가져오기
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게시글이 존재하지 않습니다."));
        return article.getMember().getId().equals(memberId);
    }

    // 게시글 추천 여부 확인
    @Override
    public boolean checkLikeArticle(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        return articleLikeRepository.findByArticleAndMember(article, member).isPresent();
    }

    // 게시글 추천 증가
    @Override
    public void articleLikeUp(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        ArticleLike articleLike = ArticleLike.builder()
            .article(article)
            .member(member)
            .build();

        articleLikeRepository.save(articleLike);
        article.likeUp();
    }

    // 게시글 추천 감소
    @Override
    public void articleLikeDown(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        ArticleLike articleLike = articleLikeRepository.findByArticleAndMember(article, member)
            .orElseThrow(() -> new RuntimeException("게시글 추천 정보를 찾을 수 없습니다."));

        articleLikeRepository.delete(articleLike);
        article.likeDown();
    }

    // appId로 게시글 조회
    @Override
    public Page<ArticleResponseDto> findByAppId(int appId, Pageable pageable) {
        return articleRepository.findByAppId(appId, pageable).map(article -> {
            int commentCount = article.getComments().size();
            return ArticleResponseDto.fromEntityWithCommentCount(article, commentCount);
        });
    }

}
