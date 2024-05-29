package mytrophy.api.article.dto;

import lombok.Getter;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;
import mytrophy.api.member.entity.Member;

@Getter
public class ArticleResponseDto {
    private Header header;
    private String name;
    private String content;
    private String imagePath;
    private Long memberId;

    public ArticleResponseDto(Article article) {
        this.header = article.getHeader();
        this.name = article.getName();
        this.content = article.getContent();
        this.imagePath = article.getImagePath();
        this.memberId = article.getMember().getId();
    }

}
