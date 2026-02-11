package com.mossy.boundedContext.in;


import com.mossy.boundedContext.app.user.UserFacade;
import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.boundedContext.in.dto.UserInfoDTO;
import com.mossy.boundedContext.in.dto.request.SignupRequest;
import com.mossy.boundedContext.out.repository.seller.SellerRequestRepository;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;

import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "User", description = "로그인 사용자 정보 조회 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final SellerRequestRepository sellerRequestRepository;
    private final UserFacade userFacade;

    @Operation(
            summary = "회원가입",
            description = "일반 유저(USER)로 가입")
    @PostMapping("/signup")
    public RsData<Long> signup(@RequestBody SignupRequest req) {
        return RsData.success(SuccessCode.SIGNUP_COMPLETE, userFacade.signup(req));
    }

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인된 사용자의 기본 식별 정보(userId)를 조회"
    )
    @GetMapping("/me")
    public RsData<UserInfoDTO> me(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Nickname") String nickname,
            @RequestHeader("X-User-Name") String name
    ) {

        Optional<SellerRequest> latestRequest =
                sellerRequestRepository.findTopByUserIdOrderByCreatedAtDesc(userId);

        SellerRequestStatus status = latestRequest
                .map(SellerRequest::getStatus)
                .orElse(null);

        UserInfoDTO dto = new UserInfoDTO(
                userId,
                nickname,
                name,
                status
        );

        return RsData.success(SuccessCode.GET_MY_INFO_COMPLETE, dto);

    }
}
