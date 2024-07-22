package mytrophy.api.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mytrophy.api.common.base.BaseEntity;
import mytrophy.api.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    private Long id;

    @Column
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String embeddingVector;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    @JsonIgnore
    private List<GameCategory> gameCategoryList = new ArrayList<>();
}
