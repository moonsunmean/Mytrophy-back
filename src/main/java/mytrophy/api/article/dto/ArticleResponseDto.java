package mytrophy.api.article.dto;

import lombok.Getter;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;
import mytrophy.api.comment.dto.CommentDto;
import mytrophy.api.comment.entity.Comment;
import mytrophy.api.member.entity.Member;

import java.util.List;

@Getter
public class ArticleResponseDto {
    private Long id;
    private Header header;
    private String name;
    private String content;
    private String imagePath;
    private Long appId;
    private Long memberId;
    private String username;
    private List<Comment> comments;
    private int commentCount;

    public ArticleResponseDto(Article article, int commentCount) {
        this.id = article.getId();
        this.header = article.getHeader();
        this.name = article.getName();
        this.content = article.getContent();
        this.imagePath = article.getImagePath();
        this.appId = article.getAppId();
        Member member = article.getMember();
        if (member != null) {
            this.memberId = member.getId();
            this.username = member.getUsername();
        }
        this.comments = article.getComments();
        this.commentCount = commentCount;
    }

    public ArticleResponseDto(Article article) {
        this.id = article.getId();
        this.header = article.getHeader();
        this.name = article.getName();
        this.content = article.getContent();
        this.imagePath = article.getImagePath();
        this.appId = article.getAppId();
        Member member = article.getMember();
        if (member != null) {
            this.memberId = member.getId();
            this.username = member.getUsername();
        }
        this.comments = article.getComments();
        // 댓글 수 초기화
        this.commentCount = commentCount;
    }

    public static ArticleResponseDto fromEntity(Article article) {
        return new ArticleResponseDto(article);
    }

    public static ArticleResponseDto fromEntityWithCommentCount(Article article, int commentCount) {
        return new ArticleResponseDto(article, commentCount);
    }
}


