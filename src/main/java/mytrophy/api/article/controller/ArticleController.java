package mytrophy.api.article.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mytrophy.api.article.dto.ArticleRequestDto;
import mytrophy.api.article.dto.ArticleResponseDto;
import mytrophy.api.article.enumentity.Header;
import mytrophy.api.article.service.ArticleService;
import mytrophy.api.image.service.ImageService;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.service.MemberService;
import mytrophy.api.querydsl.service.ArticleQueryService;
import mytrophy.global.jwt.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor // final 필드를 파라미터로 받는 생성자를 생성
@Slf4j
public class ArticleController {

    private final ArticleService articleService;
    private final ImageService imageService;
    private final ArticleQueryService articleQueryService;
    private final MemberService memberService;

    // 게시글 생성
    @PostMapping("/articles")
    public ResponseEntity<ArticleResponseDto> createArticle(@AuthenticationPrincipal CustomUserDetails userInfo,
                                                 @RequestBody ArticleRequestDto articleRequestDto,
                                                 @RequestParam(value = "imagePath", required = false) List<String> imagePath) throws IOException {
        //토큰에서 username 빼내기
        String username = userInfo.getUsername();
        Member member = memberService.findMemberByUsername(username);
        log.info("memberId: {}", member.getId());

        // 이미지 업로드 및 경로 설정
        if (imagePath != null) {
            articleRequestDto.setImagePath(String.join(",", imagePath));
        }

        // Article 생성
        ArticleResponseDto articleResponseDto = articleService.createArticle(member.getId(), articleRequestDto, imagePath);

        return ResponseEntity.status(HttpStatus.CREATED).body(articleResponseDto);
    }

    // 게시글 리스트 조회
    @GetMapping("/articles")
    public ResponseEntity<List<ArticleResponseDto>> getAllArticles() {
        List<ArticleResponseDto> articles = articleService.findAll();
        return ResponseEntity.ok().body(articles);
    }

    // 해당 게시글 조회
    @GetMapping("/articles/{id}")
    public ResponseEntity<List<ArticleResponseDto>> getArticleById(@PathVariable("id") Long id) {
        List<ArticleResponseDto> articleResponseDto = articleQueryService.findArticleWithCommentsOrderedByLatest(id);
        return ResponseEntity.ok().body(articleResponseDto);
    }

    // 말머리 별 게시글 리스트 조회
    @GetMapping("/articles/headers/{header}")
    public ResponseEntity<List<ArticleResponseDto>> getArticlesByHeader(@PathVariable Header header) {
        List<ArticleResponseDto> articles;
        // 헤더가 유효한지 검사
        switch (header) {
            case FREE_BOARD:
            case INFORMATION:
            case GUIDE:
            case REVIEW:
            case CHATING:
                // 유효한 헤더인 경우 해당 헤더로 게시글 조회
                articles = articleService.findAllByHeader(header);
                return ResponseEntity.ok().body(articles);
            default:
                // 잘못된 헤더인 경우 예외 처리
                return ResponseEntity.badRequest().build();
        }
    }

    // 말머리 별 해당 게시글 조회
    @GetMapping("/articles/{id}/headers/{header}")
    public ResponseEntity<ArticleResponseDto> getArticleByHeaderAndId(@PathVariable("id") Long id, @PathVariable("header") Header header) {
        ArticleResponseDto article = articleService.findByIdAndHeader(id, header);

        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(article);
    }


    // 게시글 수정
    @PatchMapping("/articles/{id}")
    public ResponseEntity updateArticle(@AuthenticationPrincipal CustomUserDetails userInfo,
                                        @PathVariable("id") Long id,
                                        @RequestBody ArticleRequestDto articleRequestDto,
                                        @RequestParam(value = "imagePath", required = false) List<String> imagePath) throws IOException {
        //토큰에서 username 빼내기
        String username = userInfo.getUsername();
        Long memberId = memberService.findMemberByUsername(username).getId();

        // 유저 권한 확인
        if (!articleService.isAuthorized(id, memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (articleRequestDto.getHeader() == null || articleRequestDto.getName() == null || articleRequestDto.getContent() == null) {
            return ResponseEntity.badRequest().build();
        }

        // 이미지 업로드 및 경로 설정
        if (imagePath != null) {
            articleRequestDto.setImagePath(String.join(",", imagePath));
        }

        // 파일을 변경하지 않았을 경우
        if (articleRequestDto.getImagePath() == null) {
            ArticleResponseDto article = articleService.findById(id);
            articleRequestDto.setImagePath(article.getImagePath());
        }

        articleService.updateArticle(memberId, id, articleRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 게시글 삭제
    @DeleteMapping("/articles/{id}")
    public ResponseEntity deleteArticle(@PathVariable("id") Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }

    // 파일만 업로드
    @PostMapping("/articles/files")
    public ResponseEntity uploadOnlyFiles(@RequestPart ("file") List<MultipartFile> files) throws IOException {
        List<String> uploadFiles = imageService.uploadFiles(files);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFiles);
    }

    // 파일만 삭제
    @DeleteMapping("/articles/files")
    public ResponseEntity removeOnlyFiles(List<String> files) {
        imageService.removeFile(files);
        return ResponseEntity.ok().build();
    }

    // 좋아요 증가
    @PatchMapping("/articles/{id}/cnt-up")
    public ResponseEntity upCntUp(@PathVariable("id") Long id) {
        articleService.upCntUp(id);
        return ResponseEntity.ok().build();
    }

    // 좋아요 감소
    @PatchMapping("/articles/{id}/cnt-down")
    public ResponseEntity CntUpDown(@PathVariable("id") Long id) {
        articleService.CntUpDown(id);
        return ResponseEntity.ok().build();
    }

}
