package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.MarketFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.request.CartItemAddRequest;
import backend.mossy.shared.market.dto.request.CartItemUpdateRequest;
import backend.mossy.shared.market.dto.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class ApiV1CartController {
    private final MarketFacade marketFacade;

    @GetMapping
    public RsData<CartResponse> getCart(@RequestParam Long userId) {
        return new RsData<>("200", "상품 조회를 성공했습니다.",marketFacade.getCart(userId));
    }

    @PostMapping("/items")
    public RsData<Void> addCartItem(
            @RequestParam Long userId,
            @RequestBody CartItemAddRequest request
    ) {
        marketFacade.addCartItem(userId, request);
        return new RsData<>("200", "상품이 장바구니에 추가되었습니다.");
    }

    @PatchMapping("/items")
    public RsData<Void> updateCartItem(
            @RequestParam Long userId,
            @RequestBody CartItemUpdateRequest request
    ) {
        marketFacade.updateCartItem(userId, request);
        return new RsData<>("200", "장바구니 상품 수량이 수정되었습니다.");
    }

    @DeleteMapping("/items/{productId}")
    public RsData<Void> removeCartItem(
            @RequestParam Long userId,
            @PathVariable Long productId
    ) {
        marketFacade.removeCartItem(userId, productId);
        return new RsData<>("200", "장바구니에서 상품이 삭제되었습니다.");
    }

    @DeleteMapping
    public RsData<Void> clearCart(@RequestParam Long userId) {
        marketFacade.clearCart(userId);
        return new RsData<>("200", "장바구니가 비워졌습니다.");
    }
}
