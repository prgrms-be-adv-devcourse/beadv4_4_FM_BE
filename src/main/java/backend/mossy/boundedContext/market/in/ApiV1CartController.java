package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.MarketFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.requets.CartItemAddRequest;
import backend.mossy.shared.market.dto.requets.CartItemUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class ApiV1CartController {
    private final MarketFacade marketFacade;

    @PostMapping("/items")
    public RsData<Void> addCartItem(
            @RequestParam Long userId,
            @RequestBody CartItemAddRequest request
    ) {
        return marketFacade.addCartItem(userId, request);
    }

    @PatchMapping("/items")
    public RsData<Void> updateCartItem(
            @RequestParam Long userId,
            @RequestBody CartItemUpdateRequest request
    ) {
        return marketFacade.updateCartItem(userId, request);
    }

    @DeleteMapping("/items/{productId}")
    public RsData<Void> removeCartItem(
            @RequestParam Long userId,
            @PathVariable Long productId
    ) {
        return marketFacade.removeCartItem(userId, productId);
    }

    @DeleteMapping
    public RsData<Void> clearCart(@RequestParam Long userId) {
        return marketFacade.clearCart(userId);
    }
}
