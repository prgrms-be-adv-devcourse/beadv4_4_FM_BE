package com.mossy.boundedContext.out;

import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.out.external.AuthFeignClient;
import com.mossy.boundedContext.out.external.dto.response.TokenIssueResponse;
import com.mossy.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApiClient {

    private final AuthFeignClient authFeignClient;

    public LoginResponse issueForSellerApproved(Long userId, Long sellerId) {
        RsData<TokenIssueResponse> response = authFeignClient.issueForSellerApproved(userId, sellerId);
        TokenIssueResponse data = response.getData();
        return new LoginResponse(data.accessToken(), data.refreshToken());
    }
}

