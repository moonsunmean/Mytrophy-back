package mytrophy.api.article.service;

import jakarta.transaction.Transactional;
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
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    // 게시글 생성
    @Override
    @Transactional // 트랜잭션 처리
    public Article createArticle(Long memberId, ArticleRequestDto articleRequestDto, List<String> imagePath) throws IOException {
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

        return articleRepository.save(article);
    }

    // 게시글 리스트 조회
    @Override
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    // 해당 게시글 조회
    @Override
    public Article findById(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        return article;
    }

    // 말머리 별 게시글 리스트 조회
    @Override
    public List<Article> findAllByHeader(Header header) {
        return articleRepository.findByHeader(header);
    }

    // 말머리 별 해당 게시글 조회
    @Override
    public Article findByIdAndHeader(Long id, Header header) {
        return articleRepository.findByIdAndHeader(id, header);
    }

    // 게시글 수정
    @Override
    @Transactional
    public Article updateArticle(Long memberId, Long id, ArticleRequestDto articleRequestDto) {
        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Article article = findById(id);

        if (article == null) {
            throw new ResourceNotFoundException("해당 게시글이 존재하지 않습니다.");
        }

        // 게시글 정보 업데이트
        article.updateArticle(articleRequestDto.getHeader(), articleRequestDto.getName(), articleRequestDto.getContent(), articleRequestDto.getImagePath());

        return articleRepository.save(article);
    }

    // 게시글 삭제
    @Override
    @Transactional
    public void deleteArticle(Long memberId, Long articleId) {
        // 로그인한 유저의 정보 가져오기
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("로그인한 회원 정보를 찾을 수 없습니다."));

        // 게시글 정보 가져오기
        Article article = findById(articleId);
        if (article == null) {
            throw new ResourceNotFoundException("해당 게시글이 존재하지 않습니다.");
        }

        // 게시글을 작성한 유저의 정보 가져오기
        Member author = article.getMember();

        // 로그인한 유저와 게시글을 작성한 유저의 ID가 일치하는지 확인
        if (member.getId().equals(author.getId())) {
            // 일치하면 게시글 삭제
            articleRepository.delete(article);
        } else {
            // 일치하지 않으면 권한 없음 예외 발생
            throw new RuntimeException("게시글 삭제 권한이 없습니다.");
        }
    }

    // 유저 권한 확인
    public boolean isAuthorized(Long id, Long memberId) {
        Article article = findById(id);
        return article.getMember().getId().equals(memberId);
    }

    // 좋아요 증가
    @Override
    @Transactional
    public void upCntUp(Long id) {
        Article article = findById(id);
        article.upCntUp();
    }

    // 좋아요 감소
    @Override
    @Transactional
    public void CntUpDown(Long id) {
        Article article = findById(id);
        article.CntUpDown();
    }

}
