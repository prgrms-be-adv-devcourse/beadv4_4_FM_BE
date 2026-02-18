package com.mossy.boundedContext.out;

import com.mossy.boundedContext.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
