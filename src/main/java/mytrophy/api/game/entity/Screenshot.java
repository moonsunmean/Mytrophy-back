package mytrophy.api.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mytrophy.api.common.base.BaseEntity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screenshot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String thumbnailImagePath;

    @Column(length = 2000)
    private String fullImagePath;


}
