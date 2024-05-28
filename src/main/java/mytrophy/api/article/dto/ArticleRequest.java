package mytrophy.api.article.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;
import mytrophy.api.comment.entity.Comment;
import mytrophy.api.member.entity.Member;

import java.util.List;

@Data
@NoArgsConstructor
public class ArticleRequest {
    private Header header;
    private String name;
    private String content;
    private String imagePath;
    private Long memberId;
    private List<Comment> comments;

    @Builder
    public ArticleRequest(Header header, String name, String content) {
        this.header = header;
        this.name = name;
        this.content = content;
    }
}
