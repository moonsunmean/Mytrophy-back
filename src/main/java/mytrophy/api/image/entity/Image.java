package mytrophy.api.image.entity;

import jakarta.persistence.*;
import lombok.Data;
import mytrophy.api.article.entity.Article;

@Entity
@Data
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imagePath;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

}
