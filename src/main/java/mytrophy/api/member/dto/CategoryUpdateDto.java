package mytrophy.api.member.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryUpdateDto {
    private List<Long> categoryIds;
}