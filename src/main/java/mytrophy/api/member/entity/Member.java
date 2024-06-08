package mytrophy.api.member.entity;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mytrophy.api.article.entity.Article;
import mytrophy.api.common.base.BaseEntity;
import mytrophy.api.game.entity.Category;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
@Getter
@Setter
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;  //여기서 username 이 로그인할 때 입력하는 로그인 아이디

    @Column(nullable = true)
    private String password;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true, unique = true)
    private String steamId; // 스팀 ID 값

    @Column(nullable = true)
    private String loginType;// 로그인 형태 (소셜로그인, 일반로그인)


    @Column(nullable = true)
    private String imagePath;;// 프로필 이미지

    @Column(nullable = false)
    private String role;// 권한 (ROLE_USER, ROLE_ADMIN)

    @Setter
    @Getter
    private boolean firstLogin = true; // 기본값을 true(첫로그인이후 false로 변경)

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("member") // Prevent circular reference
    private List<Article> articles = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Category> categories;

}
