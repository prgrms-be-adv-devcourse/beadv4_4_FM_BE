package com.mossy.boundedContext.out.product;

import com.mossy.boundedContext.domain.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>  {


}
