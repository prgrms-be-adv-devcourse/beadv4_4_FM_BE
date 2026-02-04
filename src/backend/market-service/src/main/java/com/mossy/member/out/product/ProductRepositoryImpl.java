package com.mossy.boundedContext.out.product;

import com.mossy.shared.market.dto.response.ProductInfoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.mossy.boundedContext.domain.cart.QCart.cart;
import static com.mossy.boundedContext.domain.cart.QCartItem.cartItem;
import static com.mossy.boundedContext.domain.product.QCategory.category;
import static com.mossy.boundedContext.domain.product.QProduct.product;
import static com.mossy.boundedContext.domain.product.QProductImage.productImage;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductInfoResponse> findCartItemsByBuyerId(Long buyerId) {
        return queryFactory
                .select(Projections.constructor(ProductInfoResponse.class,
                        product.id,
                        product.seller.id,
                        product.name,
                        category.name,
                        product.price,
                        product.weight,
                        productImage.imageUrl,
                        cartItem.quantity
                ))
                .from(cart)
                .join(cart.items, cartItem)
                .join(product)
                    .on(product.id.eq(cartItem.productId))
                .leftJoin(product.category, category)
                .leftJoin(productImage)
                    .on(productImage.product.eq(product)
                    .and(productImage.isThumbnail.isTrue()))
                .where(cart.buyer.id.eq(buyerId))
                .fetch();
    }
}
