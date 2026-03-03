package com.mossy.boundedContext.app;

import com.mossy.boundedContext.global.jwt.JwtProvider;
import com.mossy.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final JwtProvider jwtProvider;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Transactional
    public void execute(String refreshToken) {
        final Long userId;
        try {
            userId = jwtProvider.getUserId(refreshToken);
        } catch (DomainException e) {
            return;
        } catch (Exception e) {
            return;
        }

        refreshTokenUseCase.delete(userId);

    }
}
