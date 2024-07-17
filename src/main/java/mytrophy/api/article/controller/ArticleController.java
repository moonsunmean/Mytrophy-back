package mytrophy.api.article.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import mytrophy.global.handler.InvalidHeaderException;
import mytrophy.global.handler.InvalidRequestException;
import mytrophy.global.handler.UnauthorizedException;
import mytrophy.global.handler.resourcenotfound.ResourceNotFoundException;
import mytrophy.global.jwt.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor // final 필드를 파라미터로 받는 생성자를 생성
@Slf4j
public class ArticleController {

    private final ArticleService articleService;
    private final ImageService imageService;
    private final ArticleQueryService articleQueryService;
    private final MemberService memberService;

    // 게시글 생성
    @PostMapping
    @Operation(summary = "게시글 생성", description = "로그인 되어있는 유저의 게시글 제목, 내용, 게임 appId, 이미지 경로를 받아 게시글을 생성한다.")
    public ResponseEntity<ArticleResponseDto> createArticle(@AuthenticationPrincipal CustomUserDetails userInfo,
                                                            @Parameter(description = "게시글 제목, 내용, 게임 appId를 입력한다.") @RequestBody ArticleRequestDto articleRequestDto,
                                                            @Parameter(description = "firebase로 업로드된 파일 경로를 입력한다.") @RequestParam(value = "imagePath", required = false) List<String> imagePath) throws IOException {
        // 토큰에서 username 빼내기
        String username = userInfo.getUsername();
        Member member = memberService.findMemberByUsername(username);

        // Article 생성
        ArticleResponseDto articleResponseDto = articleService.createArticle(member.getId(), articleRequestDto, imagePath);

        // Http Status 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(articleResponseDto);
    }


    // 게시글 리스트 조회
    @GetMapping
    @Operation(summary = "게시글 리스트 조회", description = "페이지 번호와 사이즈를 입력받아 해당 페이지의 게시글 리스트를 조회한다.")
    public ResponseEntity<Page<ArticleResponseDto>> getAllArticles(@PageableDefault(size = 10) Pageable pageable,
                                                                   @Parameter(description = "param으로 memberId를 입력하면 해당 Id가 생성한 게시글 리스트가 조회된다.") @RequestParam(required = false) Long memberId,
                                                                   @RequestParam(required = false, defaultValue = "false") boolean cntUp) {
        Page<ArticleResponseDto> articles = articleService.getArticles(pageable, memberId, cntUp);
        return ResponseEntity.ok().body(articles);
    }

    // 게시글 수 조회
    @GetMapping("/count")
    @Operation(summary = "게시글 수 조회", description = "게시글 수를 조회한다.")
    public ResponseEntity<Long> getArticleCount() {
        long count = articleService.getArticleCount();
        return ResponseEntity.ok(count);
    }

    // 해당 게시글 조회
    @GetMapping("/{id}")
    @Operation(summary = "게시글 조회", description = "게시글 id를 입력받아 해당 게시글을 조회한다.")
    public ResponseEntity<ArticleResponseDto> getArticleById(@PathVariable("id") Long id) {
        ArticleResponseDto articleResponseDto = articleQueryService.findArticleWithCommentsOrderedByLatest(id);
        return ResponseEntity.ok().body(articleResponseDto);
    }

    // 말머리 별 게시글 리스트 조회
    @GetMapping("/headers/{header}")
    @Operation(summary = "말머리 별 게시글 리스트 조회", description = "말머리를 입력받아 해당 말머리의 게시글 리스트를 조회한다.")
    public ResponseEntity<Page<ArticleResponseDto>> getArticlesByHeader(@PathVariable Header header,
                                                                        @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<ArticleResponseDto> articles = articleService.findAllByHeader(header, pageable);
            return ResponseEntity.ok().body(articles);
        } catch (InvalidHeaderException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글 수정
    @PatchMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "게시글 id를 입력받아 해당 게시글을 수정한다.")
    public ResponseEntity<?> updateArticle(@AuthenticationPrincipal CustomUserDetails userInfo,
                                           @Parameter(description = "수정하려는 게시글 id를 입력한다.") @PathVariable("id") Long id,
                                           @Parameter(description = "게시글의 내용을 입력한다.") @RequestBody ArticleRequestDto articleRequestDto,
                                           @Parameter(description = "firebase에 업로드 된 이미지 경로를 입력한다.") @RequestParam(value = "imagePath", required = false) List<String> imagePath) throws IOException {
        String username = userInfo.getUsername();
        Long memberId = memberService.findMemberByUsername(username).getId();
        try {
            articleService.updateArticle(memberId, id, articleRequestDto, imagePath);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글 id를 입력받아 해당 게시글을 삭제한다.")
    public ResponseEntity deleteArticle(@PathVariable("id") Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }

    // 파일만 업로드
    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "파일 업로드", description = "MultipartFile로 파일을 firebase에 업로드 시켜 URL을 가져온다")
    public ResponseEntity uploadOnlyFiles(@Parameter(description = "업로드할 파일을 MultipartFile로 받습니다.") @RequestPart ("file") List<MultipartFile> files) throws IOException {
        List<String> uploadFiles = imageService.uploadFiles(files);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFiles);
    }

    // 게시글 추천
    @PostMapping("/{id}/like")
    @Operation(summary = "게시글 추천", description = "게시글 id를 입력받아 해당 게시글을 추천한다.")
    public ResponseEntity<String> likeArticle(@Parameter(description = "추천하려는 게시글 id를 입력한다.") @PathVariable("id") Long articleId,
                                              @AuthenticationPrincipal CustomUserDetails userInfo) {
        String username = userInfo.getUsername();
        Long memberId = memberService.findMemberByUsername(username).getId();
        try {
            String responseMessage = articleService.toggleArticleLike(articleId, memberId);
            return ResponseEntity.ok().body(responseMessage);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest(). body("추천 실패");
        }
    }

    // appId로 게시글 조회
    @GetMapping("/appId/{appId}")
    @Operation(summary = "게임별 게시글 조회", description = "게임 appId를 입력받아 해당 게임의 게시글 리스트를 조회한다.")
    public ResponseEntity<Page<ArticleResponseDto>> getArticleByAppId(@PathVariable("appId") int appId,
                                                                      @PageableDefault(size = 10) Pageable pageable) {
        // 페이지 번호를 조정하여 데이터 조회
        Page<ArticleResponseDto> article = articleService.findByAppId(appId, PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize()));
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(article);
    }

    // memberId로 좋아요 누른 게시물 조회
    @GetMapping("/liked/{memberId}")
    @Operation(summary = "좋아요 누른 게시글 조회", description = "회원 id를 입력받아 해당 회원이 좋아요 누른 게시글 리스트를 조회한다.")
    public ResponseEntity<Page<ArticleResponseDto>> getLikedArticles(@AuthenticationPrincipal CustomUserDetails userInfo,
                                                                     @Parameter(description = "해당 회원의 id를 입력한다.") @PathVariable Long memberId,
                                                                     @PageableDefault(size = 10) Pageable pageable) {
        //토큰에서 username 빼내기
        String username = userInfo.getUsername();
        Member member = memberService.findMemberByUsername(username);

        Page<ArticleResponseDto> likedArticles = articleQueryService.getLikedArticlesByMemberId(memberId, pageable);
        return ResponseEntity.ok().body(likedArticles);
    }

    // 게시글 검색
    @GetMapping("/keyword-search")
    @Operation(summary = "게시글 검색", description = "검색어를 입력받아 해당 검색어가 포함된 게시글 리스트를 조회한다.")
    public ResponseEntity<Page<ArticleResponseDto>> searchArticles(@Parameter(description = "검색어를 입력한다.") @RequestParam String keyword,
                                                                   @Parameter(description = "검색할 타겟을 입력한다.") @RequestParam(required = false, defaultValue = "name") String target,
                                                                   @PageableDefault(size = 10) Pageable pageable) {
        Page<ArticleResponseDto> articles = articleService.findByNameArticles(keyword, target, pageable);
        return ResponseEntity.ok().body(articles);
    }
}
