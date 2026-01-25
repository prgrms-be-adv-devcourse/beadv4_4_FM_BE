package backend.mossy.boundedContext.market.app.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    String DIR_NAME = "product";

    // 여러 파일 업로드
    public List<String> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        return files.stream()
                .map(this::uploadFile)
                .toList();
    }

    // 단일 파일 업로드
    public String uploadFile(MultipartFile file) {
        String fileName = DIR_NAME +"/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build()).toString();

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 중 오류 발생: " + file.getOriginalFilename(), e);
        }
    }

    // 단일 파일 삭제
    public void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        // URL에서 Key 추출
        String key = extractKeyFromUrl(imageUrl);

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", imageUrl, e);
        }
    }

    // 여러 파일 삭제
    public void deleteFiles(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;
        imageUrls.forEach(url -> {
            log.info("S3 파일 삭제 시도: {}", url);
            deleteFile(url);
        });
    }

    private String extractKeyFromUrl(String imageUrl) {
        try {
            // 경로 추출
            String path = new java.net.URL(imageUrl).getPath();

            String key = path.startsWith("/") ? path.substring(1) : path;

            //문자열 디코딩
            return URLDecoder.decode(key, StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("URL에서 Key 추출 및 디코딩 실패: {}", imageUrl);
            return null;
        }
    }
}
