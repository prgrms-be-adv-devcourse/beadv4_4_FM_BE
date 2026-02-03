package com.mossy.boundedContext.out.order;

import com.mossy.boundedContext.domain.order.WeightGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WeightGradeRepository extends JpaRepository<WeightGrade, Long> {

    @Query("SELECT w FROM WeightGrade w ORDER BY w.maxWeight ASC NULLS LAST")
    List<WeightGrade> findAllByOrderByMaxWeightAsc();
}