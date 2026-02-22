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
    public void processRefund(Long orderItemId, BigDecimal refundAmount, BigDecimal buyerPaidAmount) {

        // 1. orderItemId로 정산 후보 조회 (PESSIMISTIC_WRITE 락)
        // 동시 환불 요청 시 이중 환불 방지
        List<PayoutCandidateItem> candidates =
                payoutCandidateItemRepository.findByRelIdAndRelTypeCodeWithLock(
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

        // 4. 환불 처리 — BUYER/PLATFORM 그룹 분리
        List<PayoutCandidateItem> buyerItems = candidates.stream()
                .filter(c -> c.getEventType() != PayoutEventType.정산__프로모션_플랫폼부담)
                .toList();

        List<PayoutCandidateItem> platformItems = candidates.stream()
                .filter(c -> c.getEventType() == PayoutEventType.정산__프로모션_플랫폼부담)
                .toList();

        refundCandidates(buyerItems, refundAmount);

        if (!platformItems.isEmpty()) {
            BigDecimal platformTotal = platformItems.stream()
                    .map(PayoutCandidateItem::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal ratio = refundAmount.divide(buyerPaidAmount, 12, RoundingMode.HALF_UP);
            BigDecimal platformRefundAmount = platformTotal.multiply(ratio).setScale(0, RoundingMode.DOWN);
            refundCandidates(platformItems, platformRefundAmount);
        }
    }

    /**
     * 정산 후보 항목들을 비율대로 상계
     */
    private void refundCandidates(
            List<PayoutCandidateItem> candidates,
            BigDecimal refundAmount
    ) {
        BigDecimal totalAmount = candidates.stream()
                .map(PayoutCandidateItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_EVENT_TYPE); 
        }

        List<BigDecimal> allocatedAmounts = new java.util.ArrayList<>();
        BigDecimal allocatedSum = BigDecimal.ZERO;

        // 금액 배분: 일단 내림 처리
        for (PayoutCandidateItem candidate : candidates) {
            BigDecimal ratio = candidate.getAmount()
                    .divide(totalAmount, 12, RoundingMode.HALF_UP);

            BigDecimal itemRefundAmount = refundAmount
                    .multiply(ratio)
                    .setScale(0, RoundingMode.DOWN);

            allocatedAmounts.add(itemRefundAmount);
            allocatedSum = allocatedSum.add(itemRefundAmount);
        }

        // 잔여금 보정: 최종 합계를 refundAmount와 정확히 일치시킴
        BigDecimal remainder = refundAmount.subtract(allocatedSum);
        if (!allocatedAmounts.isEmpty() && remainder.compareTo(BigDecimal.ZERO) != 0) {
            int lastIndex = allocatedAmounts.size() - 1;
            allocatedAmounts.set(lastIndex, allocatedAmounts.get(lastIndex).add(remainder));
        }

        // 음수 환불 항목 저장
        for (int i = 0; i < candidates.size(); i++) {
            PayoutCandidateItem candidate = candidates.get(i);

            BigDecimal ratio = candidate.getAmount()
                    .divide(totalAmount, 12, RoundingMode.HALF_UP);

            BigDecimal itemRefundCarbon = candidate.getCarbonKg()
                    .multiply(ratio)
                    .setScale(2, RoundingMode.HALF_UP);

            PayoutCandidateItem refundItem = payoutMapper.createRefundItem(
                    candidate,
                    convertToRefundEventType(candidate.getEventType()),
                    allocatedAmounts.get(i).negate(),
                    itemRefundCarbon.negate()
            );

            payoutCandidateItemRepository.save(refundItem);
        }
    }

    /**
     * 정산 타입 → 환불 타입 변환
     */
    private PayoutEventType convertToRefundEventType(PayoutEventType original) {
        return switch (original) {
            case 정산__상품판매_대금 -> PayoutEventType.정산__상품환불_대금;
            case 정산__상품판매_수수료 -> PayoutEventType.정산__상품환불_수수료;
            case 정산__상품판매_기부금 -> PayoutEventType.정산__상품환불_기부금;
            case 정산__프로모션_플랫폼부담 -> PayoutEventType.정산__상품환불_플랫폼부담;
            default -> throw new DomainException(
                    ErrorCode.INVALID_PAYOUT_EVENT_TYPE
            );
        };
    }
}
