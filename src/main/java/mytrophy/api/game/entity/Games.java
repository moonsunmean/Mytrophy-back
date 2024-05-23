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
public class Games {

    // 게임 목록의 id를 그대로 entity 저장
    @Id
    private Long id;

    @Column
    private String name;

    @OneToOne(mappedBy = "Games", fetch = FetchType.LAZY)
    GameDetails gameDetails;

}
