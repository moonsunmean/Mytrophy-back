package mytrophy.api.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String detailedDescription;

    @Column
    private String shortcutDescription;

    @Column
    private String explanation;

    @Column
    private String developers;

    @Column
    private String publishers;

    @Column
    private String pcRequirements;

    @Column
    private int price;

    @Column
    private LocalDateTime releaseDate;

    @Column
    private int recommendations;

    @Column
    private String headerImagePath;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameDetails")
    private List<GameCategories> gameCategories;

    // 카테고리를 제외한 나머지는 단방향 맵핑
    @OneToMany(fetch = FetchType.LAZY)
    private List<Achievements> achievements;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Screenshots> screenshots;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Languages> languages;


}
