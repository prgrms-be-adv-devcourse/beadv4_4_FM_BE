//package com.mossy.boundedContext.wishList.in;
//
//import com.mossy.boundedContext.wishList.app.WishListFacade;
//import com.mossy.boundedContext.wishList.in.dto.response.WishlistResponse;
//import com.mossy.global.rsData.RsData;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/wishlist")
//@RequiredArgsConstructor
//@Tag(name = "Wishlist", description = "찜 API")
//public class ApiV1WishListApiController {
//    private final WishListFacade wishListFacade;
//
//    @Operation(
//            summary = "찜 추가",
//            description = "상품을 찜 목록에 추가합니다."
//    )
//    @PostMapping
//    public RsData<Long> createWishList(
//            @Parameter(description = "사용자 ID", required = true)
//            @RequestParam(name = "userId") Long userId,
//
//            @Parameter(description = "상품 ID", required = true)
//            @RequestParam(name = "productId") Long productId
//    ) {
//        return new RsData<>("200", "찜 완료.", wishListFacade.addWishList(userId, productId));
//    }
//
//    @Operation(
//            summary = "찜 삭제",
//            description = "찜 목록에서 상품을 삭제합니다."
//    )
//    @DeleteMapping
//    public RsData<Void> deleteWishList(
//            @Parameter(description = "사용자 ID", required = true)
//            @RequestParam(name = "userId") Long userId,
//
//            @Parameter(description = "상품 ID", required = true)
//            @RequestParam(name = "productId") Long productId
//    ) {
//        wishListFacade.deleteWishList(userId, productId);
//        return new RsData<>("200", "찜 삭제 완료.");
//    }
//
//    @Operation(
//            summary = "찜 목록 조회",
//            description = "사용자의 찜 목록을 조회합니다."
//    )
//    @GetMapping
//    public RsData<List<WishlistResponse>> getWishList(
//            @Parameter(description = "사용자 ID", required = true)
//            @RequestParam(name = "userId") Long userId
//    ) {
//        return new RsData<>("200", "찜 목록 조회 성공.", wishListFacade.getWishList(userId));
//    }
//
//    @Operation(
//            summary = "찜 여부 확인",
//            description = "특정 상품의 찜 여부를 확인합니다."
//    )
//    @GetMapping("/check")
//    public RsData<Boolean> checkWishList(
//            @Parameter(description = "사용자 ID", required = true)
//            @RequestParam(name = "userId") Long userId,
//
//            @Parameter(description = "상품 ID", required = true)
//            @RequestParam(name = "productId") Long productId
//    ) {
//        return new RsData<>("200", "찜 여부 확인 완료.", wishListFacade.checkWishList(userId, productId));
//    }
//}
