//package com.mossy.boundedContext.wishList.out;
//
//import com.mossy.boundedContext.wishList.in.dto.response.WishlistResponse;
//import com.querydsl.core.types.Projections;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//
//import java.util.List;
//
//import static com.mossy.boundedContext.product.domain.QCategory.category;
//import static com.mossy.boundedContext.product.domain.QProduct.product;
//import static com.mossy.boundedContext.product.domain.QProductImage.productImage;
//import static com.mossy.boundedContext.wishList.domain.QWishlist.wishlist;
//
//@RequiredArgsConstructor
//public class WishlistRepositoryImpl implements WishlistRepositoryCustom {
//
//    private final JPAQueryFactory queryFactory;
//
//    @Override
//    public List<WishlistResponse> findWishlistByUserId(Long userId) {
//        return queryFactory
//                .select(Projections.constructor(WishlistResponse.class,
//                        wishlist.id,
//                        product.id,
//                        product.name,
//                        category.name,
//                        product.price,
//                        productImage.imageUrl
//                ))
//                .from(wishlist)
//                .join(wishlist.product, product)
//                .join(product.category, category)
//                .leftJoin(product.images, productImage)
//                    .on(productImage.isThumbnail.isTrue())
//                .where(wishlist.marketUser.id.eq(userId))
//                .orderBy(wishlist.createdAt.desc())
//                .fetch();
//    }
//}
