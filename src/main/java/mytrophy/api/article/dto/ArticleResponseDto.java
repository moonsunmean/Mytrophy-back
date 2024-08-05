package mytrophy.api.article.dto;

import lombok.Getter;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;
import mytrophy.api.comment.dto.CommentDto;
import mytrophy.api.common.base.BaseEntity;
import mytrophy.api.member.dto.MemberDto;
import mytrophy.api.member.entity.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ArticleResponseDto extends BaseEntity {
    private Long id;
    private Header header;
    private String name;
    private String content;
    private Integer partySize;
    private String partyTime;
    private String partyOption;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imagePath;
    private int cntUp;
    private Long appId;
    private Long memberId;
    private String username;
    private String nickname;
    private String memberImage;
    private List<CommentDto> comments;
    private int commentCount;

    public ArticleResponseDto(Article article, int commentCount) {
        this.id = article.getId();
        this.header = article.getHeader();
        this.name = article.getName();
        this.content = article.getContent();
        this.partySize = article.getPartySize();
        this.partyTime = article.getPartyTime();
        this.partyOption = article.getPartyOption();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
        this.imagePath = article.getImagePath();
        this.cntUp = article.getCntUp();
        this.appId = article.getAppId();
        Member member = article.getMember();
        if (member != null) {
            this.memberId = member.getId();
            this.username = member.getUsername();
            this.nickname = member.getNickname();
            this.memberImage = member.getImagePath();
        }

        //지연로딩 에러 해결 -> comments를 commentDto로 변환해서 할당
        this.comments = article.getComments() != null ? article.getComments().stream()
            .map(CommentDto::new) // CommentDto 생성자 사용
            .collect(Collectors.toList()) : new ArrayList<>();

        this.commentCount = article.getCommentCount();
    }

    public ArticleResponseDto(Article article) {
        this.id = article.getId();
        this.header = article.getHeader();
        this.name = article.getName();
        this.content = article.getContent();
        this.partySize = article.getPartySize();
        this.partyTime = article.getPartyTime();
        this.partyOption = article.getPartyOption();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
        this.imagePath = article.getImagePath();
        this.cntUp = article.getCntUp();
        this.appId = article.getAppId();
        Member member = article.getMember();
        if (member != null) {
            this.memberId = member.getId();
            this.username = member.getUsername();
            this.nickname = member.getNickname();
            this.memberImage = member.getImagePath();
        }

        this.comments = article.getComments() != null ? article.getComments().stream()
            .map(CommentDto::new) // CommentDto 생성자 사용
            .collect(Collectors.toList()) : new ArrayList<>();

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
