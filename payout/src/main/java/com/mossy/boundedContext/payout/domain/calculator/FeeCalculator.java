package com.mossy.boundedContext.payout.domain.calculator;

import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateCreateDto;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;

import com.mossy.shared.payout.enums.CarbonGrade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * [Domain Service] 주문 아이템의 수수료를 계산하는 도메인 서비스
 * 수수료는 주문금액의 20% 고정
 */
@Component
@RequiredArgsConstructor
public class FeeCalculator {

    private final CarbonCalculator carbonCalculator;

    // 고정 수수료율 (20%)
    private static final BigDecimal FEE_RATE = new BigDecimal("0.20");

    /**
     * 주어진 주문 아이템에 대한 수수료를 계산
     * 수수료 = 주문금액 × 20% (고정)
     *
     * @param dto 수수료 계산의 기준이 되는 정산 후보 DTO
     * @return 계산된 수수료 (원 단위로 반올림됨)
     */
    public BigDecimal calculate(PayoutCandidateCreateDto dto) {
        validateOrderItem(dto);

        // 주문금액에 고정 수수료율(20%)을 적용하여 수수료 계산
        // 원 단위로 반올림 처리
        return dto.orderPrice()
                .multiply(FEE_RATE)
                .setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 주어진 주문 아이템의 탄소 등급을 조회
     *
     * @param dto 탄소 등급을 조회할 정산 후보 DTO
     * @return 계산된 탄소 등급
     */
    public CarbonGrade getGrade(PayoutCandidateCreateDto dto) {
        BigDecimal carbon = carbonCalculator.calculate(dto);
        return CarbonGrade.fromCarbon(carbon);
    }

    /**
     * 주어진 주문 아이템에 대한 총 탄소 배출량(kg 단위)을 계산하여 반환
     *
     * @param dto 탄소 배출량을 계산할 정산 후보 DTO
     * @return 계산된 탄소 배출량 (kg 단위)
     */
    public BigDecimal getCarbon(PayoutCandidateCreateDto dto) {
        return carbonCalculator.calculate(dto);
    }

    private void validateOrderItem(PayoutCandidateCreateDto dto) {
        if (dto == null) {
            throw new DomainException(ErrorCode.INVALID_DONATION_CALCULATION_INPUT);
        }
        if (dto.orderPrice() == null) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_FEE);
        }
    }
}
