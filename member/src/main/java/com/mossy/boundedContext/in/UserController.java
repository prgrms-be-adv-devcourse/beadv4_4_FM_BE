package com.mossy.boundedContext.in;


import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.global.config.UserDetailsImpl;
import com.mossy.boundedContext.in.dto.UserInfoDTO;
import com.mossy.boundedContext.out.seller.SellerRequestRepository;
import com.mossy.global.rsData.RsData;

import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "User", description = "로그인 사용자 정보 조회 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final SellerRequestRepository sellerRequestRepository;

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인된 사용자의 기본 식별 정보(userId)를 조회"
    )
    @GetMapping("/me")
    public RsData<UserInfoDTO> me(@AuthenticationPrincipal UserDetailsImpl principal) {

        Long userId = principal.getUserId();

        Optional<SellerRequest> latestRequest =
                sellerRequestRepository.findTopByUserIdOrderByCreatedAtDesc(userId);

        SellerRequestStatus status = latestRequest
                .map(SellerRequest::getStatus)
                .orElse(null);

        UserInfoDTO dto = new UserInfoDTO(
                userId,
                principal.getNickname(),
                principal.getName(),
                status
        );

        return RsData.success("내 정보 조회 성공", dto);

    }
}
