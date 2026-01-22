package backend.mossy.boundedContext.market.out.product;

import backend.mossy.shared.market.dto.response.ProductInfoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static backend.mossy.boundedContext.market.domain.cart.QCart.cart;
import static backend.mossy.boundedContext.market.domain.cart.QCartItem.cartItem;
import static backend.mossy.boundedContext.market.domain.product.QCategory.category;
import static backend.mossy.boundedContext.market.domain.product.QProduct.product;
import static backend.mossy.boundedContext.market.domain.product.QProductImage.productImage;

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
                .join(product.category, category)
                .join(productImage)
                    .on(productImage.product.eq(product)
                    .and(productImage.isThumbnail.isTrue()))
                .where(cart.buyer.id.eq(buyerId))
                .fetch();
    }
}