package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.exception.ErrorCode;
import com.mossy.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SuspendUserUseCase {

    private final UserRepository userRepository;

    @Transactional
    public void suspend(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));
        user.suspend();
    }

    @Transactional
    public void activate(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));
        user.activate();
    }
}
