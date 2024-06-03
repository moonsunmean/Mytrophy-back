package mytrophy.api.game.entity;

import jakarta.persistence.*;
import lombok.*;
import mytrophy.api.common.base.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Integer appId;

    @Column(length = 2000)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column
    private String developer;

    @Column
    private String publisher;

    @Column(length = 2000)
    private String requirement;

    @Column
    private Integer price;

    @Column
    private LocalDate releaseDate;

    @Column
    private Integer recommendation;

    @Column(length = 2000)
    private String headerImagePath;

    @Column
    private Boolean koIsPosible;

    @Column
    private Boolean enIsPosible;

    @Column
    private Boolean jpIsPosible;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "game")
    private List<GameCategory> gameCategoryList = new ArrayList<>();

    // 카테고리를 제외한 나머지는 단방향 맵핑
    @OneToMany(fetch = FetchType.LAZY)
    private List<Achievement> achievementList;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Screenshot> screenshotList;
}
