package com.mossy.boundedContext.out;

import com.mossy.boundedContext.in.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {
    private final AuthClient authClient;

    public LoginResponse issueForSellerApproved(Long userId, Long sellerId) {
        return authClient.issueForSellerApproved(userId, sellerId);
    }
}
