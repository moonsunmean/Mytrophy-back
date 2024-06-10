package mytrophy.api.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String content;
    private Long memberId;
    private Long articleId;
    private int likes;
    private Long parentCommentId;

    public CommentDto(Long id, String content, Long memberId, Long articleId, int likes, Long parentCommentId) {
        this.id = id;
        this.content = content;
        this.memberId = memberId;
        this.articleId = articleId;
        this.likes = likes;
        this.parentCommentId = parentCommentId;
    }
}
