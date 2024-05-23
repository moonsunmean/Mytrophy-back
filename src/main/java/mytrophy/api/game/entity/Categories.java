package mytrophy.api.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categories {

    @Id
    private Long id;

    @Column
    private String koName;

    @Column
    private String enName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<GameCategories> gameCategories;
}
