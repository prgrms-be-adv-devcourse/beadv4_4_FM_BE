package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.in.dto.response.ProductInfoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.mossy.boundedContext.cart.domain.QCart.cart;
import static com.mossy.boundedContext.cart.domain.QCartItem.cartItem;
import static com.mossy.boundedContext.product.domain.QCategory.category;
import static com.mossy.boundedContext.product.domain.QProduct.product;
import static com.mossy.boundedContext.product.domain.QProductImage.productImage;

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
