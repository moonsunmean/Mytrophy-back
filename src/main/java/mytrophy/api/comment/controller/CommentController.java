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
                                                    @AuthenticationPrincipal CustomUserDetails userinfo) {
        //토큰에서 username 빼내기
        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        CommentDto createdComment = commentService.createComment(memberId, articleId, createCommentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    //댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("commentId") Long commentId,
                                                    @RequestBody String content,
                                                    @AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        CommentDto updatedComment = commentService.updateComment(commentId, memberId, content);
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

    //게시글 댓글 조회
    @GetMapping("/articles/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByArticleId(@PathVariable("id") Long articleId){
        List<CommentDto> comments = commentService.findByArticleId(articleId);
        return ResponseEntity.ok(comments);
    }

    //회원별 댓글 조회
    @GetMapping("/members/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByMemberId(@AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        List<CommentDto> comments = commentService.findByMemberId(memberId);
        return ResponseEntity.ok(comments);
    }

    //게시글별 댓글 수 조회
    @GetMapping("/articles/{id}/count")
    public ResponseEntity<Integer> countCommentsByArticleId(@PathVariable("id") Long articleId) {
        int count = commentService.countByArticleId(articleId);
        return ResponseEntity.ok(count);
    }

    //댓글 추천
    @PostMapping("/comments/{id}/like")
    public ResponseEntity<String> likeComment(@PathVariable("id") Long commentId,
                                              @AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        commentService.likeComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }

    //댓글 추천 취소
    @PostMapping("comments/{id}/unlike")
    public ResponseEntity<String> unlikeComment(@PathVariable("id") Long commentId,
                                                @AuthenticationPrincipal CustomUserDetails userinfo) {

        String username = userinfo.getUsername();
        Long memberId = memberRepository.findByUsername(username).getId();

        commentService.unlikeComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }

}
