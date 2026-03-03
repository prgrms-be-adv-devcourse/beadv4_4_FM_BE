package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.global.ut.EncryptionUtils;
import com.mossy.boundedContext.in.dto.request.ChangeAddressRequest;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeAddressUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;

    @Transactional
    public void execute(Long userId, ChangeAddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        // 소셜 전용 계정은 비밀번호 확인 불가 → 먼저 설정해야 함
        if (user.isSocialOnly()) {
            throw new DomainException(ErrorCode.SOCIAL_ONLY_ACCOUNT);
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new DomainException(ErrorCode.INVALID_PASSWORD);
        }

        user.changeAddress(encryptionUtils.encrypt(request.address()));
    }
}

