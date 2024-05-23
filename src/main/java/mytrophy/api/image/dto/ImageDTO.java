package mytrophy.api.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
public class ImageDTO {

    private String fileName; // 파일 이름
    private String uuid; // 파일 고유 식별자
    private String folderPath; // 파일 경로

    public String getImageURL() {
        return URLEncoder.encode(folderPath+ "/" + uuid + "_" + fileName, StandardCharsets.UTF_8);
    }
}
