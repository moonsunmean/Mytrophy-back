package mytrophy.api.article.dto;

import lombok.Getter;
import mytrophy.api.article.domain.Header;

@Getter
public class ArticleRequest {
    private Header header;
    private String name;
    private String content;

    public ArticleRequest(Header header, String name, String content) {
        this.header = header;
        this.name = name;
        this.content = content;
    }
}
