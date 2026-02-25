package com.mossy.boundedContext.in;

import com.mossy.boundedContext.app.user.AdminUserFacade;
import com.mossy.boundedContext.in.dto.response.BuyerSummaryResponse;
import com.mossy.boundedContext.in.dto.response.SellerSummaryResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - User", description = "관리자 회원 관리 API")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class ApiV1AdminUserController {

    private final AdminUserFacade adminUserFacade;

    @Operation(summary = "구매자 목록 조회", description = "판매자 역할이 없는 일반 회원 목록을 조회합니다.")
    @GetMapping("/buyers")
    public RsData<Page<BuyerSummaryResponse>> getBuyers(
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<BuyerSummaryResponse> response = adminUserFacade.getBuyers(pageable);
        return RsData.success(SuccessCode.GET_BUYERS_COMPLETE, response);
    }

    @Operation(summary = "판매자 목록 조회", description = "승인된 판매자 목록을 조회합니다.")
    @GetMapping("/sellers")
    public RsData<Page<SellerSummaryResponse>> getSellers(
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<SellerSummaryResponse> response = adminUserFacade.getSellers(pageable);
        return RsData.success(SuccessCode.GET_SELLERS_COMPLETE, response);
    }

    @Operation(summary = "회원 정지", description = "특정 회원을 정지 처리합니다.")
    @PatchMapping("/{userId}/suspend")
    public RsData<Void> suspendUser(@PathVariable Long userId) {
        adminUserFacade.suspendUser(userId);
        return RsData.success(SuccessCode.SUSPEND_USER_COMPLETE);
    }

    @Operation(summary = "회원 정지 해제", description = "정지된 회원을 활성화합니다.")
    @PatchMapping("/{userId}/activate")
    public RsData<Void> activateUser(@PathVariable Long userId) {
        adminUserFacade.activateUser(userId);
        return RsData.success(SuccessCode.ACTIVATE_USER_COMPLETE);
    }
}
