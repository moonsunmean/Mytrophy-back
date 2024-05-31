package mytrophy.api.article.service;

import mytrophy.api.article.dto.ArticleResponseDto;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    // 게시글 생성
    @Override
    @Transactional // 트랜잭션 처리
    public ArticleResponseDto createArticle(Long memberId, ArticleRequestDto articleRequestDto, List<String> imagePath) throws IOException {
        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Article article;

        // 이미지 경로가 null이 아닌 경우
        if (imagePath != null && !imagePath.isEmpty()) {
            article = Article.builder()
                .header(articleRequestDto.getHeader())
                .name(articleRequestDto.getName())
                .content(articleRequestDto.getContent())
                .imagePath(articleRequestDto.getImagePath()) // 이미지 경로 설정
                .member(member)
                .build();
        } else {
            // 이미지 경로가 null이거나 비어있는 경우
            article = Article.builder()
                .header(articleRequestDto.getHeader())
                .name(articleRequestDto.getName())
                .content(articleRequestDto.getContent())
                .member(member)
                .build();
        }

        Article savedArticle = articleRepository.save(article);

        // 생성된 게시글을 ResponseDto로 변환하여 반환
        return ArticleResponseDto.fromEntity(savedArticle);
    }

    // 게시글 리스트 조회
    @Override
    @Transactional
    public List<ArticleResponseDto> findAll() {
        List<Article> articles = articleRepository.findAll();
        return articles.stream()
            .map(article -> {
                int commentCount = article.getComments().size();
                return ArticleResponseDto.fromEntityWithCommentCount(article, commentCount);
            })
            .collect(Collectors.toList());
    }

    // 해당 게시글 조회
    @Override
    @Transactional
    public ArticleResponseDto findById(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        return ArticleResponseDto.fromEntity(article);
    }

    // 말머리 별 게시글 리스트 조회
    @Override
    @Transactional
    public List<ArticleResponseDto> findAllByHeader(Header header) {
        List<Article> articles = articleRepository.findByHeader(header);
        return articles.stream()
            .map(article -> {
                int commentCount = article.getComments().size();
                return ArticleResponseDto.fromEntityWithCommentCount(article, commentCount);
            })
            .collect(Collectors.toList());
    }

    // 말머리 별 해당 게시글 조회
    @Override
    @Transactional
    public ArticleResponseDto findByIdAndHeader(Long id, Header header) {
        Article article = articleRepository.findByIdAndHeader(id, header);
        if (article == null) {
            throw new ResourceNotFoundException("해당 게시글이 존재하지 않습니다.");
        }
        return ArticleResponseDto.fromEntity(article);
    }

    // 게시글 수정
    @Override
    @Transactional
    public ArticleResponseDto updateArticle(Long memberId, Long id, ArticleRequestDto articleRequestDto) {
        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 게시글 조회
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게시글이 존재하지 않습니다."));

        // 게시글 정보 업데이트
        article.updateArticle(articleRequestDto.getHeader(), articleRequestDto.getName(), articleRequestDto.getContent(), articleRequestDto.getImagePath());

        // 엔티티를 저장하고, 저장된 엔티티를 기반으로 DTO 객체 생성하여 반환
        return ArticleResponseDto.fromEntity(articleRepository.save(article));
    }


    // 게시글 삭제
    @Override
    @Transactional
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

    // 좋아요 증가
    @Override
    @Transactional
    public void upCntUp(Long id) {
        // 게시글 정보 가져오기
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게시글이 존재하지 않습니다."));
        article.upCntUp();
    }

    // 좋아요 감소
    @Override
    @Transactional
    public void CntUpDown(Long id) {
        // 게시글 정보 가져오기
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게시글이 존재하지 않습니다."));
        article.CntUpDown();
    }

}
