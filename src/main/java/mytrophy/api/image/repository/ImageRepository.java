package mytrophy.api.image.repository;

import mytrophy.api.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<String> deleteByImagePath(String imagePath);
}
