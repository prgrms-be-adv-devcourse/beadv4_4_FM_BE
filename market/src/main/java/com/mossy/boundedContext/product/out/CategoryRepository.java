package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>  {


}
