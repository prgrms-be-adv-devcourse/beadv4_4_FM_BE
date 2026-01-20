package backend.mossy.boundedContext.market.out.order;

import backend.mossy.boundedContext.market.domain.order.WeightGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeightGradeRepository extends JpaRepository<WeightGrade, Long> {
    Optional<WeightGrade> findByWeightGradeName(String weightGradeName);
}