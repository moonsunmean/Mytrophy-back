package mytrophy.api.game.entity;

import jakarta.persistence.*;
import lombok.*;
import mytrophy.api.common.base.BaseEntity;
import mytrophy.api.game.enums.Positive;

import java.time.LocalDate;
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

    @Column(length = 1000)
    @Enumerated(EnumType.STRING)
    private Positive positive;

    @Column(length = 2000)
    private String headerImagePath;

    @Column
    private Boolean koIsPossible;

    @Column
    private Boolean enIsPossible;

    @Column
    private Boolean jpIsPossible;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameCategory> gameCategoryList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Achievement> achievementList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screenshot> screenshotList = new ArrayList<>();


}
