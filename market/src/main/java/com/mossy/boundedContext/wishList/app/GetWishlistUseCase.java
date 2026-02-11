//package com.mossy.boundedContext.wishList.app;
//
//import com.mossy.boundedContext.wishList.in.dto.response.WishlistResponse;
//import com.mossy.boundedContext.wishList.out.WishlistRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class GetWishlistUseCase {
//
//    private final WishlistRepository wishlistRepository;
//
//    @Transactional(readOnly = true)
//    public List<WishlistResponse> getWishlist(Long userId) {
//        return wishlistRepository.findWishlistByUserId(userId);
//    }
//}
