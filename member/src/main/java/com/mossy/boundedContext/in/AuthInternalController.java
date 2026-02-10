package com.mossy.boundedContext.in;

import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.out.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/internal")
@RequiredArgsConstructor
public class AuthInternalController {
    private final AuthFacade authFacade;

    @PostMapping("/issue-seller-token")
    public LoginResponse issueForSellerApproved(
            @RequestParam Long userId, @RequestParam Long sellerId) {
        return authFacade.issueForSellerApproved(userId, sellerId);
    }
}
