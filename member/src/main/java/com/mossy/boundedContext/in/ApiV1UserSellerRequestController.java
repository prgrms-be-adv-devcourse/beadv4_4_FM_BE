package com.mossy.boundedContext.in;


import com.mossy.boundedContext.app.seller.SellerRequestUserFacade;
import com.mossy.boundedContext.in.dto.request.SellerRequestCreateRequest;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@Tag(name = "Seller Request", description = "판매자 신청(User) API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ApiV1UserSellerRequestController {

    private final SellerRequestUserFacade sellerRequestUserFacade;

    @Operation(summary = "판매자 신청", description = "판매자 신청서를 생성하고 상태를 PENDING으로 저장")
    @PostMapping(value = "/seller-request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RsData<Long> requestSeller(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestPart("data") SellerRequestCreateRequest req,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        Long requestId = sellerRequestUserFacade.request(userId, req, profileImage);
        return RsData.success(SuccessCode.SELLER_REQUEST_COMPLETE, requestId);
    }
}
