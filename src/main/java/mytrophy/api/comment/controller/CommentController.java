package mytrophy.api.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import mytrophy.api.comment.dto.CommentDto;
import mytrophy.api.comment.dto.CreateCommentDto;
import mytrophy.api.comment.service.CommentService;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.global.jwt.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final MemberRepository memberRepository;

    //댓글 등록
    @Operation(summary = "댓글 등록", description = "댓글을 작성합니다. (paraentCommentId가 null인 댓글에만 대댓글을 달 수 있음)")
    @PostMapping("/articles/{id}/comments")
    public ResponseEntity<CommentDto> createComment(@Parameter(description = "댓글을 달고자 하는 게시글의 ID", required = true) @PathVariable("id") Long articleId,
                                                    @RequestBody CreateCommentDto createCommentDto,
                                                    @Parameter(description = "대댓글의 경우 부모 댓글의 ID", required = false) @RequestParam(value = "parentCommentId", required = false) Long parentCommentId,
                                                    @AuthenticationPrincipal CustomUserDetails userinfo) {
        //토큰에서 username 빼내기
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        CommentDto createdComment = commentService.createComment(memberId, articleId, createCommentDto, parentCommentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    //댓글 수정
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다. (권한: 본인)")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@Parameter(description = "수정하고자 하는 댓글의 ID", required = true) @PathVariable("commentId") Long commentId,
                                                    @RequestBody CreateCommentDto createCommentDto,
                                                    @AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        CommentDto updatedComment = commentService.updateComment(commentId, memberId, createCommentDto.getContent());
        return ResponseEntity.ok(updatedComment);
    }

    //댓글 삭제
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. (권한: 본인)")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@Parameter(description = "삭제하고자 하는 댓글의 ID", required = true) @PathVariable("commentId") Long commentId,
                                              @AuthenticationPrincipal CustomUserDetails userinfo){

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.noContent().build();
    }

    //회원별 댓글 조회
    @Operation(summary = "특정 회원의 댓글 조회", description = "특정 회원이 작성한 댓글과 대댓글을 조회합니다.")
    @GetMapping("/members/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByMemberId(@AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        List<CommentDto> comments = commentService.findByMemberId(memberId);
        return ResponseEntity.ok(comments);
    }

    //댓글 좋아용
    @Operation(summary = "특정 댓글에 좋아요 등록", description = "특정 댓글에 좋아요를 남깁니다. (좋아요를 남긴 이력이 있다면 좋아요 취소)")
    @PostMapping("/comments/{id}/like")
    public ResponseEntity<Void> toggleLikeComment(@Parameter(description = "좋아요를 남기고자 하는 댓글의 ID", required = true) @PathVariable("id") Long commentId,
                                                  @AuthenticationPrincipal CustomUserDetails userinfo) {
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();
        commentService.toggleLikeComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }

}
