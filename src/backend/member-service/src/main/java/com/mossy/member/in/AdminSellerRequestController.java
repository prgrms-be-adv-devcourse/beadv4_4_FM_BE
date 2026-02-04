package com.mossy.member.in;

import com.mossy.member.auth.app.AuthFacade;
import com.mossy.member.auth.in.dto.LoginResponse;
import com.mossy.member.app.seller.SellerRequestAdminFacade;
import com.mossy.global.rsData.RsData;
import com.mossy.shared.member.dto.request.SellerApproveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
