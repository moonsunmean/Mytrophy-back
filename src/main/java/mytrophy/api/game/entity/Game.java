package mytrophy.api.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    // 게임 목록의 id를 그대로 entity 저장
    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private String detailedDescription;

    @Column
    private String shortcutDescription;

    @Column
    private String explain;

    @Column
    private String developers;

    @Column
    private String publishers;

    @Column
    private String suppotedLanguages;

    @Column
    private String pcRequirements;

    @Column
    private String price;

    @Column
    private LocalDateTime releaseDate;

    @Column
    private int recommendations;

    @Column
    private String headerImagePath;

}
