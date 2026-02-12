package com.mossy.boundedContext.payout.app.common;

import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.out.repository.PayoutCandidateItemRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.payout.enums.PayoutEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * [UseCase] 환불 처리를 담당하는 서비스 클래스
 * OrderRefundedEvent 발생 시 정산 후보 항목을 상계 처리
 */
@Service
@RequiredArgsConstructor
public class PayoutRefundUseCase {

    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final PayoutMapper payoutMapper;

    /**
     * 환불 처리: orderItem의 정산 후보를 조회하여 비율대로 상계
     *
     * @param orderItemId 환불할 orderItem ID
     * @param refundAmount 환불 금액
     * @throws DomainException 정산 후보를 찾을 수 없거나 이미 정산 완료된 경우
     */
    @Transactional
    public void processRefund(Long orderItemId, BigDecimal refundAmount) {

        // 1. orderItemId로 정산 후보 조회
        List<PayoutCandidateItem> candidates =
                payoutCandidateItemRepository.findByRelIdAndRelTypeCode(
                        orderItemId, "OrderItem"
                );

        // 2. 조회 결과 검증
        if (candidates.isEmpty()) {
            throw new DomainException(
                    ErrorCode.PAYOUT_CANDIDATE_NOT_FOUND);

        }

        // 3. 정산 완료 여부 체크
        boolean isPayoutCompleted = candidates.stream()
                .anyMatch(c -> c.getPayoutItem() != null);

        if (isPayoutCompleted) {
            throw new DomainException(
                    ErrorCode.REFUND_NOT_ALLOWED_AFTER_PAYOUT_COMPLETED
            );
        }

        // 4. 환불 처리
        refundCandidates(candidates, refundAmount);
    }

    /**
     * 정산 후보 항목들을 비율대로 상계
     */
    private void refundCandidates(
            List<PayoutCandidateItem> candidates,
            BigDecimal refundAmount
    ) {
        // 총액 계산
        BigDecimal totalAmount = candidates.stream()
                .map(PayoutCandidateItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 각 항목별로 비율 적용해서 환불
        candidates.forEach(candidate -> {

            // 비율 계산
            BigDecimal ratio = candidate.getAmount()
                    .divide(totalAmount, 4, RoundingMode.HALF_UP);

            // 환불 금액 (비율 적용)
            BigDecimal itemRefundAmount = refundAmount
                    .multiply(ratio)
                    .setScale(0, RoundingMode.HALF_UP);

            // 탄소 배출량 (비율 적용)
            BigDecimal itemRefundCarbon = candidate.getCarbonKg()
                    .multiply(ratio)
                    .setScale(2, RoundingMode.HALF_UP);

            // 음수 항목 생성 (상계)
            PayoutCandidateItem refundItem = payoutMapper.createRefundItem(
                    candidate,
                    convertToRefundEventType(candidate.getEventType()),
                    itemRefundAmount.negate(),
                    itemRefundCarbon.negate()
            );

            payoutCandidateItemRepository.save(refundItem);
        });
    }

    /**
     * 정산 타입 → 환불 타입 변환
     */
    private PayoutEventType convertToRefundEventType(PayoutEventType original) {
        return switch (original) {
            case 정산__상품판매_대금 -> PayoutEventType.정산__상품환불_대금;
            case 정산__상품판매_수수료 -> PayoutEventType.정산__상품환불_수수료;
            case 정산__상품판매_기부금 -> PayoutEventType.정산__상품환불_기부금;
            default -> throw new DomainException(
                    ErrorCode.INVALID_PAYOUT_EVENT_TYPE
            );
        };
    }
}
