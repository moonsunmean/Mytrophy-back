package mytrophy.api.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String content;
    private Long memberId;
    private Long articleId;
    private int cntUp;

    public CommentDto(Long id, String content, Long memberId, Long articleId, int cntUp) {
        this.id = id;
        this.content = content;
        this.memberId = memberId;
        this.articleId = articleId;
        this.cntUp = cntUp;
    }
}
