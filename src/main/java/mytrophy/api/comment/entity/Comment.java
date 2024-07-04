package mytrophy.api.comment.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mytrophy.api.article.entity.Article;
import mytrophy.api.common.base.BaseEntity;
import mytrophy.api.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    @JsonIgnore
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Comment parentComment;     //부모 댓글

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> childrenComment = new ArrayList<>();      //자식 댓글

    // 좋아요 리스트
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likeList = new ArrayList<>();

    //좋아요 수
    @Column(name = "likes", nullable = false)
    private int likes = 0;

    //댓글 추천
    public void incrementLikes(){
        this.likes++;
    }

    //댓글 추천 취소
    public void decrementLikes(){
        this.likes = Math.max(0, this.likes - 1);
    }

    //setter는 불변성 보장 x -> 사용 자제해야함. 대신에 Builder 패턴 사용
    @Builder
    public Comment(String content, Member member, Article article, Comment parentComment) {
        Preconditions.checkNotNull(content, "내용 필수 작성");    //null 이면 IllegalArgumentException

        // jpa 쓰니까 id는 세팅할 필요없음
        this.content = content;
        this.member = member;
        this.article = article;
        this.parentComment = parentComment;
        this.childrenComment = new ArrayList<>();
        this.likeList = new ArrayList<>();
    }

    //댓글 내용 업데이트
    public void updateContent(String content) {
        Preconditions.checkNotNull(content, "내용 필수 작성");
        this.content = content;
    }
}

