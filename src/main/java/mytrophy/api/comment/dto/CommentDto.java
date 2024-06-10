package mytrophy.api.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import mytrophy.api.comment.entity.Comment;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String content;
    private Long memberId;
    private Long articleId;
    private int likes;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imagePath;   //프로필이미지
    private String nickname;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.memberId = comment.getMember().getId();
        this.articleId = comment.getArticle().getId();
        this.likes = comment.getLikes();
        this.parentCommentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.imagePath = comment.getMember().getImagePath();
        this.nickname = comment.getMember().getNickname();
    }
}
