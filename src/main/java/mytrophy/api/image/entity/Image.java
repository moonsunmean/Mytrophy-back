package mytrophy.api.image.entity;

import jakarta.persistence.*;
import mytrophy.api.article.entity.Article;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imagePath;
    private String name;

    @ManyToOne
    private Article article;


}
