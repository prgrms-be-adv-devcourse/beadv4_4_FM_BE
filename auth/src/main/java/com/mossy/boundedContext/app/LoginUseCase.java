package com.mossy.boundedContext.app;

import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.out.MemberServiceClient;
import com.mossy.shared.auth.domain.request.MemberVerifyRequest;
import com.mossy.shared.auth.domain.response.MemberVerifyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final MemberServiceClient memberServiceClient;

    public LoginContext execute(String email, String password) {
        MemberVerifyResponse response = memberServiceClient.verify(
                new MemberVerifyRequest(email, password)
        );

        if (response == null || !response.isValid()) {
            throw new DomainException(ErrorCode.INVALID_CREDENTIALS);
        }

        String role = response.roles().isEmpty() ? "USER" : response.roles().get(0).name();
        return new LoginContext(response.userId(), role);
    }

    public record LoginContext(Long userId, String role) {}
}
