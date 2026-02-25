package com.mossy.boundedContext.in;

import com.mossy.boundedContext.out.AuthApiClient;
import com.mossy.boundedContext.app.seller.SellerRequestAdminFacade;
import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.in.dto.response.SellerApproveResponse;
import com.mossy.boundedContext.in.dto.response.SellerRequestListDto;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "판매자 신청(Admin) API")
@RestController
@RequestMapping("/api/v1/admin/seller-requests")
@RequiredArgsConstructor
public class ApiV1AdminSellerRequestController {

    private final SellerRequestAdminFacade sellerRequestAdminFacade;
    private final AuthApiClient authApiClient;

    @Operation(summary = "대기 중인 판매자 신청 목록 조회")
    @GetMapping
    public RsData<List<SellerRequestListDto>> getPendingRequests() {
        List<SellerRequestListDto> requests = sellerRequestAdminFacade.getPendingRequests();
        return RsData.success(SuccessCode.GET_SELLER_REQUESTS_COMPLETE, requests);
    }

    @Operation(summary = "판매자 신청 승인")
    @PatchMapping("/{id}/approve")
    public RsData<SellerApproveResponse> approve(@PathVariable Long id) {
        SellerRequestAdminFacade.SellerAppoveResult result = sellerRequestAdminFacade.approve(id);

        try {
            LoginResponse tokens = authApiClient.issueForSellerApproved(result.userId(), result.sellerId());
            return RsData.success(SuccessCode.SELLER_APPROVE_COMPLETE,
                    new SellerApproveResponse(result.sellerId(), tokens.accessToken(), tokens.refreshToken())
            );
        } catch (Exception e) {
            // 외부 서비스 호출 실패 시 롤백
            sellerRequestAdminFacade.rollbackApprove(result.sellerId(), result.userId());
            throw e;
        }
    }

    @Operation(summary = "판매자 신청 반려")
    @PatchMapping("/{id}/reject")
    public RsData<Void> reject(@PathVariable Long id) {
        sellerRequestAdminFacade.reject(id);
        return RsData.success(SuccessCode.SELLER_REJECT_COMPLETE, null);
    }
}
