package com.mossy.boundedContext.in;

import com.mossy.boundedContext.out.AuthFacade;
import com.mossy.boundedContext.app.seller.SellerRequestAdminFacade;
import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.in.dto.response.SellerApproveResponse;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "판매자 신청(Admin) API")
@RestController
@RequestMapping("/api/v1/admin/seller-requests")
@RequiredArgsConstructor
public class AdminSellerRequestController {

    private final SellerRequestAdminFacade sellerRequestAdminFacade;
    private final AuthFacade authFacade;

    @Operation(summary = "판매자 신청 승인")
    @PatchMapping("/{id}/approve")
    public RsData<SellerApproveResponse> approve(@PathVariable Long id) {
        SellerRequestAdminFacade.SellerAppoveResult result = sellerRequestAdminFacade.approve(id);

        LoginResponse tokens = authFacade.issueForSellerApproved(result.userId(), result.sellerId());

        return RsData.success("판매자 승인 완료",
                new SellerApproveResponse(result.sellerId(), tokens.accessToken(), tokens.refreshToken())
        );
    }

    @Operation(summary = "판매자 신청 반려")
    @PatchMapping("/{id}/reject")
    public RsData<Void> reject(@PathVariable Long id) {
        sellerRequestAdminFacade.reject(id);
        return RsData.success("판매자 반려 완료", null);
    }
}
