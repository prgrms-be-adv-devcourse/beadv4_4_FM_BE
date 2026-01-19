package backend.mossy.boundedContext.market.out;

import backend.mossy.shared.market.dto.response.CartItemResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static backend.mossy.boundedContext.market.domain.QCart.cart;
import static backend.mossy.boundedContext.market.domain.QCartItem.cartItem;
import static backend.mossy.boundedContext.market.domain.QProduct.product;
import static backend.mossy.boundedContext.market.domain.QProductImage.productImage;

@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CartItemResponse> findCartItemsByBuyerId(Long buyerId) {
        return queryFactory
                .select(Projections.constructor(CartItemResponse.class,
                        product.id,
                        product.name,
                        product.categoryId,
                        product.price,
                        productImage.imageUrl,
                        cartItem.quantity
                ))
                .from(cart)
                .join(cart.items, cartItem)
                .join(product)
                    .on(product.id.eq(cartItem.productId))
                .join(productImage)
                    .on(productImage.product.eq(product)
                    .and(productImage.isThumbnail.isTrue()))
                .where(cart.buyer.id.eq(buyerId))
                .fetch();
    }
}