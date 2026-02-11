//package com.mossy.boundedContext.cart.app;
//
//import com.mossy.boundedContext.cart.in.dto.request.CartItemAddRequest;
//import com.mossy.boundedContext.cart.in.dto.request.CartItemUpdateRequest;
//import com.mossy.boundedContext.cart.in.dto.response.CartResponse;
//import com.mossy.boundedContext.marketUser.in.dto.command.MarketUserDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class CartFacade {
//    private final CreateCartUseCase createCartUseCase;
//    private final AddCartItemUseCase addCartItemUseCase;
//    private final UpdateCartItemQuantityUseCase updateCartItemUseCase;
//    private final RemoveCartItemUseCase removeCartItemUseCase;
//    private final ClearCartUseCase clearCartUseCase;
//    private final GetCartItemListUseCase getCartItemListUseCase;
//
//    public CartResponse getCart(Long userId) {
//        return getCartItemListUseCase.getCart(userId);
//    }
//
//    public void createCart(MarketUserDto buyer) {
//        createCartUseCase.create(buyer);
//    }
//
//    public void addCartItem(Long userId, CartItemAddRequest request) {
//        addCartItemUseCase.addItem(userId, request);
//    }
//
//    public void updateCartItem(Long userId, CartItemUpdateRequest request) {
//        updateCartItemUseCase.updateItemQuantity(userId, request);
//    }
//
//    public void removeCartItem(Long userId, Long productId) {
//        removeCartItemUseCase.removeItem(userId, productId);
//    }
//
//    public void clearCart(Long userId) {
//        clearCartUseCase.clear(userId);
//    }
//
//    public void removeCartItems(Long userId, List<Long> productIds) {
//        removeCartItemUseCase.removeItems(userId, productIds);
//    }
//}
