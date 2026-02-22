package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.in.dto.request.ChangeNicknameRequest;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeNicknameUseCase {

    private final UserRepository userRepository;

    @Transactional
    public void execute(Long userId, ChangeNicknameRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        user.changeNickname(request.nickname());
    }
}

