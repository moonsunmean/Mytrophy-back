package mytrophy.api.image.service;

import jakarta.transaction.Transactional;
import mytrophy.api.article.domain.Article;
import mytrophy.api.article.repository.ArticleRepository;
import mytrophy.api.image.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class ImageService {

    private ImageRepository imageRepository;
    private ArticleRepository articleRepository;

    // 이미지 불러오기
    @Transactional
    public void loadImage(Article article, MultipartFile file) throws Exception{
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + "\\src\\main\\resources\\static\\files";
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(filePath, fileName);
        file.transferTo(saveFile);
        articleRepository.save(article);
    }


}
