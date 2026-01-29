package backend.mossy.boundedContext.market.out.product;

import backend.mossy.boundedContext.market.domain.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
