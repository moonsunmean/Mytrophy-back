package mytrophy.api.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentDto {

    private Long id;
    private String content;
    private Long memberId;
    private int cntUp;

    public CreateCommentDto(Long id, String content, Long memberId, int cntUp) {
        this.id = id;
        this.content = content;
        this.memberId = memberId;
        this.cntUp = cntUp;
    }
}
