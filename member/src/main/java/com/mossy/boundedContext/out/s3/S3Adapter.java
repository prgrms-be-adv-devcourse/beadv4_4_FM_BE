package com.mossy.boundedContext.out.s3;

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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Adapter {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String PROFILE_DIR = "profile";

    //프로필 이미지 업로드
    public String uploadProfileImage(MultipartFile file) {
        String fileName = PROFILE_DIR + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

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
            throw new RuntimeException("프로필 이미지 업로드 중 오류 발생: " + file.getOriginalFilename(), e);
        }
    }

    //S3 파일 삭제 (기본 이미지는 삭제하지 않음)
    public void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        String key = extractKeyFromUrl(imageUrl);
        if (key == null || key.toLowerCase().contains("default")) return;

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
            log.info("S3 프로필 이미지 삭제 완료: {}", key);
        } catch (Exception e) {
            log.error("S3 프로필 이미지 삭제 실패: {}", imageUrl, e);
        }
    }

    private String extractKeyFromUrl(String imageUrl) {
        try {
            String path = new java.net.URL(imageUrl).getPath();
            String key = path.startsWith("/") ? path.substring(1) : path;
            return URLDecoder.decode(key, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("URL에서 Key 추출 실패: {}", imageUrl);
            return null;
        }
    }
}

