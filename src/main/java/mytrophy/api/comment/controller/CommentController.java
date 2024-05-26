package mytrophy.api.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mytrophy.api.comment.dto.CommentDto;
import mytrophy.api.comment.dto.CreateCommentDto;
import mytrophy.api.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    //댓글 등록
    @PostMapping("/articles/{id}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable("id") Long articleId, @RequestBody CreateCommentDto createCommentDto) {
        CommentDto createdComment = commentService.createComment(articleId, createCommentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    //댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("commentId") Long commentId, @RequestParam("memberId") Long memberId, @RequestBody String content) {
        if (!commentService.isAuthorizedToUpdate(commentId, memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden 에러 반환
        }

        CommentDto updatedComment = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(updatedComment);
    }

    //댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId, @RequestParam("memberId") Long memberId){
        if (!commentService.isAuthorizedToUpdate(commentId, memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden 에러 반환
        }

        commentService.deleteComment(commentId);
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
    public ResponseEntity<List<CommentDto>> getCommentsByMemberId(@PathVariable("id") Long memberId) {
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
    //회원 연결하면 로직 수정하기 -> 앱실행할때마다 중복 초기화됨
    @PostMapping("/comments/{id}/like")
    public ResponseEntity<String> likeComment(@PathVariable("id") Long commentId, @RequestBody Long memberId) {
        try {
            commentService.likeComment(commentId, memberId);
            return ResponseEntity.ok("댓글을 추천했습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 추천한 댓글입니다.");
        }
    }

    //댓글 추천 취소
    @PostMapping("comments/{id}/unlike")
    public ResponseEntity<String> unlikeComment(@PathVariable("id") Long commentId, @RequestBody Long memberId) {
        try {
            commentService.unlikeComment(commentId, memberId);
            return ResponseEntity.ok("댓글 추천이 취소되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 댓글을 추천하지 않았습니다.");
        }
    }

}
