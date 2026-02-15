package com.mossy.boundedContext.category.out;

import com.mossy.boundedContext.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>  {


}
