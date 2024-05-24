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
    //세션에서 추천 로직 확인해서 중복추천 방지 추가하기
    @PostMapping("/comments/{id}/like")
    public ResponseEntity<Void> likeComment(@PathVariable("id") Long commentId, @RequestBody Long memberId) {
        commentService.likeComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }

    //댓글 추천 취소
    @PostMapping("comments/{id}/unlike")
    public ResponseEntity<Void> unlikeComment(@PathVariable("id") Long commentId, @RequestBody Long memberId) {
        commentService.unlikeComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }

}
