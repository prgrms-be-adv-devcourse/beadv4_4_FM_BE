package backend.mossy.boundedContext.market.out;


import backend.mossy.boundedContext.market.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findTop10ByOrderByIdDesc();
}
