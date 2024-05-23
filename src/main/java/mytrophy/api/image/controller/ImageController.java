package mytrophy.api.image.controller;

import lombok.Value;
import lombok.extern.log4j.Log4j2;
import mytrophy.api.image.dto.ImageDTO;
import mytrophy.api.image.service.ImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@Log4j2 // 로깅을 위한 어노테이션
public class ImageController {

    // 이미지 생성
    @PostMapping("/images")
    public ResponseEntity<List<ImageDTO>> uploadFile(MultipartFile[] files) {
        List<ImageDTO> imageDTOList = new ArrayList<>();

        for (MultipartFile uploadFile : files) {
            if (!Objects.requireNonNull(uploadFile.getContentType()).startsWith("image")) {
                log.warn("This file is not image type");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 이미지 파일이 아닌 경우 403 Forbidden 반환
            }

            String originalName = uploadFile.getOriginalFilename();
            assert originalName != null;
            String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1); // 파일 이름만 추출

            log.info("fileName: " + fileName);

            String uuid = UUID.randomUUID().toString();

            String saveName = uuid + "_" + fileName;
            Path savePath = Paths.get("/path/to/upload/directory" + saveName);

            String folderPath = "/files/" + fileName;

            try {
                uploadFile.transferTo(savePath);
                imageDTOList.add(new ImageDTO(fileName, uuid, folderPath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(imageDTOList, HttpStatus.OK);
    }

    // 업로드 이미지 불러오기
    @GetMapping("/images")
    public ResponseEntity<byte[]> getFile(String fileName) {
        ResponseEntity<byte[]> result;

        try {
            String srcFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            log.info("fileName: " + srcFileName);
            File file = new File(File.separator + srcFileName);
            log.info("file: " + file);
            HttpHeaders header = new HttpHeaders();

            header.add("Content-type", Files.probeContentType(file.toPath()));
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}
