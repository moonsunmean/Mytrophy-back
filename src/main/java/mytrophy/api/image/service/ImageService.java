package mytrophy.api.image.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    // 파일 업로드
    List<String> uploadFiles(List<MultipartFile> files) throws IOException;

    // 파일 삭제
    void fileRemove(List<String> files);

}
