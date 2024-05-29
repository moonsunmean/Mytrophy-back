package mytrophy.api.article.dto;

import lombok.Getter;
import mytrophy.api.article.enumentity.Header;

@Getter
public class ArticleResponseDto {
    private Long id;
    private Header header;
    private String name;
    private String content;
    private String imagePath;


    public ArticleResponseDto(Long id, Header header, String name, String content, String imagePath) {
        this.id = id;
        this.header = header;
        this.name = name;
        this.content = content;
        this.imagePath = imagePath;
    }

}
