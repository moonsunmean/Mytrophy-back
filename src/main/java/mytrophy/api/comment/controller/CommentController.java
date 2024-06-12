package mytrophy.api.comment.controller;

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
    @PostMapping("/articles/{id}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable("id") Long articleId,
                                                    @RequestBody CreateCommentDto createCommentDto,
                                                    @RequestParam(value = "parentCommentId", required = false) Long parentCommentId,
                                                    @AuthenticationPrincipal CustomUserDetails userinfo) {
        //토큰에서 username 빼내기
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        CommentDto createdComment = commentService.createComment(memberId, articleId, createCommentDto, parentCommentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    //댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("commentId") Long commentId,
                                                    @RequestBody CreateCommentDto createCommentDto,
                                                    @AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        CommentDto updatedComment = commentService.updateComment(commentId, memberId, createCommentDto.getContent());
        return ResponseEntity.ok(updatedComment);
    }

    //댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId,
                                              @AuthenticationPrincipal CustomUserDetails userinfo){

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.noContent().build();
    }

    //회원별 댓글 조회
    @GetMapping("/members/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByMemberId(@AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        List<CommentDto> comments = commentService.findByMemberId(memberId);
        return ResponseEntity.ok(comments);
    }

    //댓글 좋아용
    @PostMapping("/comments/{id}/like")
    public ResponseEntity<Void> toggleLikeComment(@PathVariable("id") Long commentId,
                                                  @AuthenticationPrincipal CustomUserDetails userinfo) {
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();
        commentService.toggleLikeComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }

}
