package backend.mossy.boundedContext.market.in.cart;

import backend.mossy.boundedContext.market.app.cart.CartFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.request.CartItemAddRequest;
import backend.mossy.shared.market.dto.request.CartItemUpdateRequest;
import backend.mossy.shared.market.dto.response.CartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class ApiV1CartController {
    private final CartFacade cartFacade;


    @Operation(
            summary = "장바구니 조회",
            description = "사용자의 장바구니와 장바구니 상품 목록을 조회합니다."
    )
    @GetMapping
    public RsData<CartResponse> getCart(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId
    ) {
        return new RsData<>("200", "장바구니 조회를 성공했습니다.", cartFacade.getCart(userId));
    }

    @Operation(
            summary = "장바구니 상품 추가",
            description = "장바구니에 상품을 추가합니다."
    )
    @PostMapping("/items")
    public RsData<Void> addCartItem(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Parameter(description = "장바구니 상품 추가 요청 DTO", required = true)
            @RequestBody CartItemAddRequest request
    ) {
        cartFacade.addCartItem(userId, request);
        return new RsData<>("200", "상품이 장바구니에 추가되었습니다.");
    }

    @Operation(
            summary = "장바구니 상품 수량 수정",
            description = "장바구니에 담긴 상품의 수량을 수정합니다."
    )
    @PatchMapping("/items")
    public RsData<Void> updateCartItem(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Parameter(description = "장바구니 상품 수량 수정 요청 DTO", required = true)
            @RequestBody CartItemUpdateRequest request
    ) {
        cartFacade.updateCartItem(userId, request);
        return new RsData<>("200", "장바구니 상품 수량이 수정되었습니다.");
    }


    @Operation(
            summary = "장바구니 상품 삭제",
            description = "장바구니에서 특정 상품을 삭제합니다."
    )
    @DeleteMapping("/items/{productId}")
    public RsData<Void> removeCartItem(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Parameter(description = "삭제할 상품 ID", required = true)
            @PathVariable Long productId
    ) {
        cartFacade.removeCartItem(userId, productId);
        return new RsData<>("200", "장바구니에서 상품이 삭제되었습니다.");
    }

    @Operation(
            summary = "장바구니 비우기",
            description = "사용자의 장바구니를 전부 비웁니다."
    )
    @DeleteMapping
    public RsData<Void> clearCart(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId
    ) {
        cartFacade.clearCart(userId);
        return new RsData<>("200", "장바구니가 비워졌습니다.");
    }
}
