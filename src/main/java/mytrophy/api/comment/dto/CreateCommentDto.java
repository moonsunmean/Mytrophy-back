package mytrophy.api.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentDto {

    private String content;
    private Long parentCommentId;

    public CreateCommentDto(String content, Long parentCommentId) {
        this.content = content;
        this.parentCommentId = parentCommentId;
    }
}
