package com.mossy.boundedContext.app;

import com.mossy.boundedContext.out.dto.request.MemberVerifyExternRequest;
import com.mossy.boundedContext.out.dto.response.MemberVerifyResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.out.external.MemberFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final MemberFeignClient memberFeignClient;

    @Transactional(readOnly = true)
    public LoginContext execute(String email, String password) {
        MemberVerifyResponse response = memberFeignClient.verify(
                new MemberVerifyExternRequest(email, password)
        );

        if (response == null || !response.isValid()) {
            throw new DomainException(ErrorCode.INVALID_CREDENTIALS);
        }

        String role = response.roles().isEmpty() ? "USER" : response.roles().get(0).name();
        return new LoginContext(response.userId(), role);
    }
    public record LoginContext(Long userId, String role) {}
}
