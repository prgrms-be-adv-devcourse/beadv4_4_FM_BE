package com.mossy.boundedContext.app;

import com.mossy.boundedContext.out.dto.request.MemberVerifyExternRequest;
import com.mossy.boundedContext.out.dto.response.MemberVerifyResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.out.external.MemberFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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

        List<String> roles = response.roles().stream()
            .map(Enum::name)
            .toList();
        Long sellerId = (roles.contains("SELLER") && response.sellerId() != null) ? response.sellerId() : null;
        return new LoginContext(response.userId(), roles, sellerId);
    }
    public record LoginContext(Long userId, List<String> roles, Long sellerId) {}
}
