package mytrophy.api.article.service;

import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.entity.ArticleLike;
import mytrophy.api.article.repository.ArticleLikeRepository;
import mytrophy.api.querydsl.repository.ArticleQueryRepository;
import mytrophy.global.handler.InvalidHeaderException;
import mytrophy.global.handler.InvalidRequestException;
import mytrophy.global.handler.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ArticleQueryRepository articleQueryRepository;

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

    // 게시글 리스트 조회 리펙토링
    public Page<ArticleResponseDto> getArticles(Pageable pageable, Long memberId, boolean cntUp) {
        Sort sort = cntUp ? Sort.by("cntUp").descending() : Sort.by("createdAt").descending();
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (memberId != null) {
            return findByMemberId(memberId, pageable);
        } else {
            return findAll(pageable);
        }
    }

    // 게시글 리스트 조회
    @Override
    public Page<ArticleResponseDto> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable)
            .map(article -> {
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
        validateHeader(header);
        Sort sort = Sort.by("createdAt").descending();
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return articleRepository.findAllByHeader(header, pageable).map(article -> {
            int commentCount = article.getComments().size();
            return ArticleResponseDto.fromEntityWithCommentCount(article, commentCount);
        });
    }

    private void validateHeader(Header header) {
        switch (header) {
            case FREE_BOARD:
            case INFORMATION:
            case GUIDE:
            case REVIEW:
            case CHATING:
                // 유효한 헤더
                break;
            default:
                // 잘못된 헤더인 경우 예외 발생
                throw new InvalidHeaderException("잘못된 헤더입니다.: " + header);
        }
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
    public void updateArticle(Long memberId, Long id, ArticleRequestDto articleRequestDto, List<String> imagePath) {

        if (!isAuthorized(id, memberId)) {
            throw new UnauthorizedException("해당 게시글에 대한 권한이 없습니다.");
        }

        validateArticleRequestDto(articleRequestDto);

        if (imagePath != null) {
            articleRequestDto.setImagePath(String.join(",", imagePath));
        } else if (articleRequestDto.getImagePath() == null) {
            ArticleResponseDto article = findById(id);
            articleRequestDto.setImagePath(article.getImagePath());
        }

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException("회원 정보를 찾을 수 없습니다."));

        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게시글이 존재하지 않습니다."));

        article.updateArticle(articleRequestDto.getHeader(), articleRequestDto.getName(),
            articleRequestDto.getContent(), articleRequestDto.getImagePath(), articleRequestDto.getAppId());

        articleRepository.save(article);
    }

    private void validateArticleRequestDto(ArticleRequestDto articleRequestDto) {
        if (articleRequestDto.getHeader() == null || articleRequestDto.getName() == null || articleRequestDto.getContent() == null) {
            throw new InvalidRequestException("필수 요소가 누락되었습니다.");
        }
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
    public String toggleArticleLike(Long articleId, Long memberId) {
        if (checkLikeArticle(articleId, memberId)) {
            articleLikeDown(articleId, memberId);
            return "게시글 추천을 취소하였습니다.";
        } else {
            articleLikeUp(articleId, memberId);
            return "게시글을 추천하였습니다.";
        }
    }

    // 좋아요 확인
    @Override
    public boolean checkLikeArticle(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        return articleLikeRepository.findByArticleAndMember(article, member).isPresent();
    }

    // 좋아요 수 증가
    @Override
    public void articleLikeUp(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        ArticleLike articleLike = ArticleLike.builder()
            .article(article)
            .member(member)
            .build();

        articleLikeRepository.save(articleLike);
        article.likeUp();
    }

    // 좋아요 수 감소
    @Override
    public void articleLikeDown(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        ArticleLike articleLike = articleLikeRepository.findByArticleAndMember(article, member)
            .orElseThrow(() -> new ResourceNotFoundException("게시글 추천 정보를 찾을 수 없습니다."));

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

    // memberId로 게시글 조회
    @Override
    public Page<ArticleResponseDto> findByMemberId(Long memberId, Pageable pageable) {
        return articleRepository.findByMemberId(memberId, pageable)
            .map(article -> {
                int commentCount = article.getComments().size();
                return ArticleResponseDto.fromEntityWithCommentCount(article, commentCount);
            });
    }
}
