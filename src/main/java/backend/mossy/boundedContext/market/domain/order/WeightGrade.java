package backend.mossy.boundedContext.market.domain.order;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "MARKET_WEIGHT_GRADE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "weight_grade_id"))
public class WeightGrade extends BaseIdAndTime {

    @Column(nullable = false)
    private String weightGradeName;

    @Column(precision = 10, scale = 3)
    private BigDecimal maxWeight;

    public static WeightGrade findByWeight(List<WeightGrade> grades, BigDecimal weight) {
        for (WeightGrade grade : grades) {
            if (grade.contains(weight)) {
                return grade;
            }
        }

        throw new DomainException(ErrorCode.WEIGHT_GRADE_NOT_FOUND);
    }

    private boolean contains(BigDecimal weight) {
        if (maxWeight == null) {
            return true;
        }
        return weight.compareTo(maxWeight) < 0;
    }
}
