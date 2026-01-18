package backend.mossy.boundedContext.market.out;

import backend.mossy.boundedContext.market.domain.Category;
import backend.mossy.boundedContext.market.domain.MarketSeller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface categoryRepository extends JpaRepository<Category, Long>  {


}
