package com.mossy.boundedContext.out.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

//애플리케이션 시작 시 기본 프로필 이미지를 S3에 업로드
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultProfileImageUploader implements CommandLineRunner {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String PROFILE_KEY = "profile/default-user.png";
    private static final String SVG_CONTENT = """
            <svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200">
              <!-- 외부 원 -->
              <circle cx="100" cy="100" r="95" fill="#D4E8D8" stroke="#5A8C6F" stroke-width="4"/>
              <!-- 머리 -->
              <circle cx="100" cy="70" r="35" fill="#A8D5A8"/>
              <!-- 어깨/하단 -->
              <ellipse cx="100" cy="140" rx="40" ry="35" fill="#A8D5A8"/>
            </svg>
            """;

    @Override
    public void run(String... args) throws Exception {
        try {
            if (!isFileExists()) {
                uploadDefaultProfileImage();
                log.info("기본 프로필 이미지가 S3에 업로드되었습니다: {}", PROFILE_KEY);
            } else {
                log.info("기본 프로필 이미지가 이미 S3에 존재합니다: {}", PROFILE_KEY);
            }
        } catch (Exception e) {
            log.error("기본 프로필 이미지 업로드 실패", e);
        }
    }

    private boolean isFileExists() {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(PROFILE_KEY)
                    .build();

            HeadObjectResponse response = s3Client.headObject(headRequest);
            return response.sdkHttpResponse().isSuccessful();
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.warn("기본 프로필 이미지 확인 중 오류: {}", e.getMessage());
            return false;
        }
    }

    private void uploadDefaultProfileImage() {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(PROFILE_KEY)
                .contentType("image/png")
                .contentLength((long) SVG_CONTENT.getBytes().length)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromString(SVG_CONTENT));
    }
}

