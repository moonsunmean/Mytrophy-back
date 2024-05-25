package mytrophy.api.article.service;


import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mytrophy.api.article.dto.ArticleRequest;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.entity.Header;
import mytrophy.api.article.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor // final 필드를 파라미터로 받는 생성자를 생성
@Slf4j // 로깅을 위한 어노테이션
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final Bucket bucket;

    // 게시글 생성
    @Transactional // 트랜잭션 처리
    public Article createArticle(ArticleRequest articleRequest, List<MultipartFile> files) throws IOException {

        Article article = Article.builder()
            .header(articleRequest.getHeader())
            .name(articleRequest.getName())
            .content(articleRequest.getContent())
            .build();

        if (files != null) {
            List<String> urls = uploadFiles(files);
            article.setImagePath(urls.toString());
        }

        return articleRepository.save(article);
    }

    // 파일 업로드
   public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        // 파일을 Bucket에 저장
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            String uuid = UUID.randomUUID().toString();
            String blobPath = "files/" + uuid;

            // 파일의 MIME 유형 가져오기
            String contentType = file.getContentType();

            // BlobInfo 객체 생성
            BlobInfo blobInfo = BlobInfo.newBuilder(bucket.getName(), blobPath)
                .setContentType(contentType)
                .build();

            // BlobInfo 객체에서 Blob 이름과 컨텐츠 유형 가져오기
            String blobName = blobInfo.getName();
            String blobContentType = blobInfo.getContentType();

            // 파일을 Bucket에 저장
            bucket.create(blobName, file.getBytes(), blobContentType);

            // 파일의 경로(urls) 추가
            String url = "https://storage.googleapis.com/" + bucket.getName() + "/" + blobPath;
            urls.add(url);
        }
        return urls;
    }

    // 파일 삭제
    public void fileRemove(List<String> files) {
        for (String imagePath : files) {
            articleRepository.deleteByImagePath(URLDecoder.decode(imagePath, StandardCharsets.UTF_8));
        }
    }

    // 게시글 리스트 조회
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    // 해당 게시글 조회
    public Article findById(Long id) {
        return articleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
    }

    // 말머리 별 게시글 리스트 조회
    public List<Article> findAllByHeader(Header header) {
        return articleRepository.findByHeader(header);
    }

    // 말머리 별 해당 게시글 조회
    public Article findByIdAndHeader(Long id, Header header) {
        return articleRepository.findByIdAndHeader(id, header);
    }

    // 게시글 수정
    @Transactional
    public Article updateArticle(Long id, Header header, String name, String content) {
        Article article = findById(id);
        article.update(header, name, content);
        return article;
    }

    // 게시글 삭제
    @Transactional
    public void deleteArticle(Long id) {
        Article article = findById(id);
        articleRepository.delete(article);
    }

    // 좋아요 증가
    @Transactional
    public void upCntUp(Long id) {
        Article article = findById(id);
        article.upCntUp();
    }


}
