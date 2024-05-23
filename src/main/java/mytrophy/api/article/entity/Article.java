package mytrophy.api.article.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mytrophy.api.common.base.BaseEntity;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자를 protected로 설정
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성되는 값으로 설정
    private Long id;

    @Enumerated(EnumType.STRING)
    private Header header;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    private int cntUp; // 좋아요 수

    @Builder // 빌더 패턴 적용
    public Article(Long id, Header header, String name, String content, int cntUp) {
        this.header = header;
        this.name = name;
        this.content = content;
        this.cntUp = cntUp;
    }

    // 게시글 생성 로직
    public static Article createArticle(Header header, String name, String content) {
        return Article.builder()
            .header(header)
            .name(name)
            .content(content)
            .cntUp(0)
            .build();
    }

    // 게시글 수정
    public void update(Header header, String name, String content) {
        this.header = header;
        this.name = name;
        this.content = content;
    }

    // 좋아요 증가
    public void upCntUp() {
        this.cntUp++;
    }
}
