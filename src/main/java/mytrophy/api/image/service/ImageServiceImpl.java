package mytrophy.api.image.service;


import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mytrophy.api.article.repository.ArticleRepository;
import mytrophy.api.image.entity.Image;
import mytrophy.api.image.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;
    private final Bucket bucket;

    // 파일 업로드
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException{
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
            // Image 엔티티 생성 및 저장
            Image image = new Image();
            image.setImagePath(url);
            imageRepository.save(image);
        }
        return urls;
    }

    // 파일 삭제
    public void fileRemove(List<String> files) {
        for (String imagePath : files) {
            imageRepository.deleteByImagePath(URLDecoder.decode(imagePath, StandardCharsets.UTF_8));
        }
    }
}
