package com.mossy.boundedContext.out;

import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.out.external.AuthFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {
    private final AuthFeignClient authFeignClient;

    public LoginResponse issueForSellerApproved(Long userId, Long sellerId) {
        return authFeignClient.issueForSellerApproved(userId, sellerId);
    }
}
