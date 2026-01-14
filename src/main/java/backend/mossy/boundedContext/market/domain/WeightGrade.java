package backend.mossy.boundedContext.market.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MARKET_WEIGHT_GRADE")
@NoArgsConstructor
@Getter
public class WeightGrade extends BaseIdAndTime {
    @Column(nullable = false)
    private String weightGradeName;
}
