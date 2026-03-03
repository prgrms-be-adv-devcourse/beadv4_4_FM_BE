package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.in.dto.request.SetPasswordRequest;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(Long userId, SetPasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        // 이미 비밀번호가 있는 계정은 이 API 사용 불가 (ChangePasswordUseCase 사용)
        if (!user.isSocialOnly()) {
            throw new DomainException(ErrorCode.PASSWORD_ALREADY_EXISTS);
        }

        user.changePassword(passwordEncoder.encode(request.newPassword()));
    }
}

