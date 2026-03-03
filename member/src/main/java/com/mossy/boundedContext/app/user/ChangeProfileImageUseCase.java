package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.boundedContext.out.s3.S3Adapter;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeProfileImageUseCase {

    private final UserRepository userRepository;
    private final S3Adapter s3Adapter;

    //프로필 이미지 변경
    @Transactional
    public String execute(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        // 기존 프로필 이미지 삭제 (default-user가 아닌 경우)
        String oldImage = user.getProfileImage();
        if (oldImage != null && !oldImage.equals("default-user")) {
            s3Adapter.deleteFile(oldImage);
        }

        // 새 이미지 업로드
        String newImageUrl = s3Adapter.uploadProfileImage(file);
        user.changeProfileImage(newImageUrl);

        return newImageUrl;
    }

    //프로필 이미지 삭제 (기본 이미지로 복원)
    @Transactional
    public void deleteAndResetToDefault(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        String oldImage = user.getProfileImage();
        if (oldImage != null && !oldImage.equals("default-user")) {
            s3Adapter.deleteFile(oldImage);
        }

        user.changeProfileImage("default-user");
    }
}
