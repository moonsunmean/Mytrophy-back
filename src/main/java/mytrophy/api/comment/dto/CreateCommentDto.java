package mytrophy.api.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentDto {

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 200, message = "Content cannot exceed 200 characters")
    private String content;

    public CreateCommentDto(String content, Long parentCommentId) {
        this.content = content;
    }
}
