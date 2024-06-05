package mytrophy.api.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mytrophy.api.common.base.BaseEntity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRead extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer lastAppId;
}
