package com.mossy.boundedContext.in;


import com.mossy.boundedContext.app.seller.SellerRequestUserFacade;
import com.mossy.global.config.UserDetailsImpl;
import com.mossy.boundedContext.in.dto.request.SellerRequestCreateRequest;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Seller Request", description = "판매자 신청(User) API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserSellerRequestController {

    private final SellerRequestUserFacade sellerRequestUserFacade;

    @Operation(summary = "판매자 신청", description = "판매자 신청서를 생성하고 상태를 PENDING으로 저장")
    @PostMapping("/seller/request")
    public RsData<Long> requestSeller(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @RequestBody @Valid SellerRequestCreateRequest req
    ) {
        Long requestId = sellerRequestUserFacade.request(principal.getUserId(), req);
        return RsData.success("판매자 신청 완료", requestId);
    }
}
