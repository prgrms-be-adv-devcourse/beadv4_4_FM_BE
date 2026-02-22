package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.global.ut.EncryptionUtils;
import com.mossy.boundedContext.in.dto.request.ProfileUpdateRequest;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCase {

    private final UserRepository userRepository;
    private final EncryptionUtils encryptionUtils;

    @Transactional
    public void execute(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(
                request.phoneNum() != null ? encryptionUtils.encrypt(request.phoneNum()) : null,
                request.address() != null ? encryptionUtils.encrypt(request.address()) : null,
                request.rrn() != null ? encryptionUtils.encrypt(request.rrn()) : null,
                request.nickname()
        );

        // 소셜 로그인 후 추가정보 입력 완료 → PENDING → ACTIVE로 변경
        if (user.isPending()) {
            user.activate();
        }
    }
}
