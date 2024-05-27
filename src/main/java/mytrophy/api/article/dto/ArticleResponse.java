package mytrophy.api.article.dto;

import mytrophy.api.article.enumentity.Header;

public class ArticleResponse {
    private Long id;
    private Header header;
    private String name;
    private String content;
    private String imagePath;

    public ArticleResponse(Long id, Header header, String name, String content, String imagePath) {
        this.id = id;
        this.header = header;
        this.name = name;
        this.content = content;
        this.imagePath = imagePath;
    }

}
