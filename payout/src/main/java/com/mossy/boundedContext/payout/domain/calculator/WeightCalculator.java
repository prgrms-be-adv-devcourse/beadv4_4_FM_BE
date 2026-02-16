package com.mossy.boundedContext.payout.domain.calculator;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * [Domain Service] 상품의 무게를 기반으로 무게 등급을 판단하는 도메인 서비스
 * 무게 구간별로 소형, 중소형, 중형, 대형 4가지 등급으로 분류
 */
@Component
public class WeightCalculator {

    private static final BigDecimal WEIGHT_SMALL_MAX = new BigDecimal("1");
    private static final BigDecimal WEIGHT_MEDIUM_SMALL_MAX = new BigDecimal("5");
    private static final BigDecimal WEIGHT_MEDIUM_MAX = new BigDecimal("10");

    /**
     * 상품의 무게를 기반으로 무게 등급을 판단
     * - 소형: 0kg ~ 1kg 이하
     * - 중소형: 1kg 초과 ~ 5kg 이하
     * - 중형: 5kg 초과 ~ 10kg 이하
     * - 대형: 10kg 초과
     *
     * @param weight 상품의 무게 (kg 단위)
     * @return 무게 등급 ("소형", "중소형", "중형", "대형")
     * @throws DomainException weight가 null이거나 0 이하인 경우
     */
    public String determineWeightGrade(BigDecimal weight) {
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCode.INVALID_CARBON_CALCULATION_INPUT);
        }

        if (weight.compareTo(WEIGHT_SMALL_MAX) <= 0) {
            return "소형";
        } else if (weight.compareTo(WEIGHT_MEDIUM_SMALL_MAX) <= 0) {
            return "중소형";
        } else if (weight.compareTo(WEIGHT_MEDIUM_MAX) <= 0) {
            return "중형";
        } else {
            return "대형";
        }
    }
}
