package backend.mossy.boundedContext.market.out.product;

import backend.mossy.boundedContext.market.domain.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>  {


}
