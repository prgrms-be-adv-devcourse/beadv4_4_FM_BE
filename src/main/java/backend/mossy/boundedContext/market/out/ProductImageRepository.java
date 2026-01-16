package backend.mossy.boundedContext.market.out;

import backend.mossy.boundedContext.market.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
