package mytrophy.api.article.dto;

import lombok.Getter;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;
import mytrophy.api.comment.dto.CommentDto;
import mytrophy.api.member.dto.MemberDto;
import mytrophy.api.member.entity.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ArticleResponseDto {
    private Long id;
    private Header header;
    private String name;
    private String content;
    private String imagePath;
    private int cntUp;
    private Long appId;
    private Long memberId;
    private String username;
    private List<CommentDto> comments;
    private int commentCount;

    public ArticleResponseDto(Article article, int commentCount) {
        this.id = article.getId();
        this.header = article.getHeader();
        this.name = article.getName();
        this.content = article.getContent();
        this.imagePath = article.getImagePath();
        this.cntUp = article.getCntUp();
        this.appId = article.getAppId();
        Member member = article.getMember();
        if (member != null) {
            this.memberId = member.getId();
        }
        this.username = member.getUsername();
        //지연로딩 에러 해결 -> comments를 commentDto로 변환해서 할당
        if (article.getComments() != null) {
            this.comments = article.getComments().stream()
                .map(comment -> new CommentDto(
                    comment.getId(),
                    comment.getContent(),
                    comment.getMember().getId(),
                    comment.getArticle().getId(),
                    comment.getLikes(),
                    comment.getParentComment() != null ? comment.getParentComment().getId() : null
                ))
                .collect(Collectors.toList());
        } else {
            this.comments = new ArrayList<>();
        }

        this.commentCount = article.getCommentCount();
    }

    public ArticleResponseDto(Article article) {
        this.id = article.getId();
        this.header = article.getHeader();
        this.name = article.getName();
        this.content = article.getContent();
        this.imagePath = article.getImagePath();
        this.cntUp = article.getCntUp();
        this.appId = article.getAppId();
        Member member = article.getMember();
        if (member != null) {
            this.memberId = member.getId();
        }
        this.username = member.getUsername();

        if (article.getComments() != null) {
            this.comments = article.getComments().stream()
                .map(comment -> new CommentDto(
                    comment.getId(),
                    comment.getContent(),
                    comment.getMember().getId(),
                    comment.getArticle().getId(),
                    comment.getLikes(),
                    comment.getParentComment() != null ? comment.getParentComment().getId() : null
                ))
                .collect(Collectors.toList());
        } else {
            this.comments = new ArrayList<>();
        }

        // 댓글 수 초기화
        this.commentCount = article.getCommentCount();
    }

    public static ArticleResponseDto fromEntity(Article article) {
        return new ArticleResponseDto(article);
    }

    public static ArticleResponseDto fromEntityWithCommentCount(Article article, int commentCount) {
        return new ArticleResponseDto(article, commentCount);
    }
}


