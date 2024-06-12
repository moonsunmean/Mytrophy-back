package mytrophy.api.comment.service;

import mytrophy.api.comment.dto.CommentDto;
import mytrophy.api.comment.dto.CreateCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(Long memberId, Long articleId, CreateCommentDto createCommentDto, Long parentCommentId);
    CommentDto updateComment(Long commentId, Long memberId, String content);

    void deleteComment(Long commentId, Long memberId);

    List<CommentDto> findByMemberId(Long memberId);

    void toggleLikeComment(Long commentId, Long memberId);

    boolean isAuthorized(Long commentId, Long memberId);
}
