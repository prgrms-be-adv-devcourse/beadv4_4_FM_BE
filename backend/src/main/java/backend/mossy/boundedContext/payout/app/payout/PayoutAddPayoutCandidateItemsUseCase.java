package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.boundedContext.payout.domain.donation.DonationCalculator;
import backend.mossy.boundedContext.payout.domain.donation.FeeCalculator;
import backend.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.payout.PayoutEventType;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;

import backend.mossy.shared.market.dto.event.OrderItemDto;
import backend.mossy.shared.market.dto.event.OrderPayoutDto;
import backend.mossy.shared.payout.dto.event.payout.CreatePayoutCandidateItemDto;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * [UseCase] 정산 후보 아이템 생성을 담당하는 서비스 클래스
 * PayoutFacade의 '1단계: 정산 후보 아이템 생성' 흐름에서 호출
 * 결제가 완료되었을 때, 해당 주문 아이템 정보를 바탕으로 미래에 정산될 항목들을 미리 생성
 */
@Service
@RequiredArgsConstructor
public class PayoutAddPayoutCandidateItemsUseCase {
    private final PayoutSupport payoutSupport;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final DonationCalculator donationCalculator;
    private final FeeCalculator feeCalculator;

    /**
     * 단일 주문 아이템(OrderItem)에 대해 정산 후보 항목을 생성
     * @param OrderPayoutDto 주문 아이템 DTO
     * @param paymentDate 결제 완료 일시
     */
    @Transactional
    public void addPayoutCandidateItem(OrderPayoutDto orderItem, LocalDateTime paymentDate) {
        if (orderItem == null) {
            throw new DomainException(ErrorCode.ORDERITEM_IS_NULL);
        }
        if (paymentDate == null) {
            throw new DomainException(ErrorCode.PAYMENT_DATE_IS_NULL);
        }
        makePayoutCandidateItems(orderItem, paymentDate);
    }

    /**
     * 하나의 주문 아이템(OrderItem)을 여러 개의 정산 후보 아이템(PayoutCandidateItem)으로 분해하여 생성
     * 예를 들어, 하나의 상품 판매는 (1)판매자에게 갈 판매대금, (2)플랫폼의 수수료, (3)기부금
     *
     * @param orderItem 처리할 개별 주문 정산 DTO
     * @param paymentDate 결제 완료 일시
     */
    private void makePayoutCandidateItems(
            OrderPayoutDto orderItem,
            LocalDateTime paymentDate
    ) {
        // --- 정산에 필요한 주요 주체(Actor)들을 조회 ---
        PayoutSeller system = payoutSupport.findSystemSeller()
                .orElseThrow(() -> new DomainException(ErrorCode.SYSTEM_SELLER_NOT_FOUND)); // 시스템(플랫폼)
        PayoutSeller donation = payoutSupport.findDonationSeller()
                .orElseThrow(() -> new DomainException(ErrorCode.DONATION_SELLER_NOT_FOUND)); // 기부금 수령처(가상 판매자)
        PayoutUser buyer = payoutSupport.findUserById(orderItem.buyerId())
                .orElseThrow(() -> new DomainException(ErrorCode.BUYER_NOT_FOUND)); // 구매자
        PayoutSeller seller = payoutSupport.findSellerById(orderItem.sellerId())
                .orElseThrow(() -> new DomainException(ErrorCode.SELLER_NOT_FOUND)); // 판매자

        // --- 금액을 계산 ---
        // 1. 탄소 배출량 기반으로 수수료를 계산
        BigDecimal payoutFee = feeCalculator.calculate(orderItem);
        if (payoutFee == null || payoutFee.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_FEE);
        }

        // 2. 이 상품 판매로 인해 발생한 기부금을 계산
        BigDecimal donationAmount = donationCalculator.calculate(orderItem);
        if (donationAmount == null || donationAmount.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_DONATION_AMOUNT);
        }

        // 3. 조정된 수수료를 계산합니다. (원래 수수료 - 기부금)
        BigDecimal adjustedFee = payoutFee.subtract(donationAmount);
        if (adjustedFee.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_FEE);
        }

        // 4. 판매 대금 계산 (주문금액 - 수수료)
        BigDecimal salePriceWithoutFee = orderItem.orderPrice().subtract(payoutFee);
        if (salePriceWithoutFee.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_FEE);
        }

        // --- 계산된 금액을 바탕으로 3가지 종류의 정산 후보 아이템을 생성 ---
        // 아이템 1: 플랫폼 수수료 (구매자 -> 시스템)
        makePayoutCandidateItem(paymentDate, orderItem, PayoutEventType.정산__상품판매_수수료, buyer, system, adjustedFee);
        // 아이템 2: 판매 대금 (구매자 -> 판매자)
        makePayoutCandidateItem(paymentDate, orderItem, PayoutEventType.정산__상품판매_대금, buyer, seller, salePriceWithoutFee);
        // 아이템 3: 기부금 (구매자 -> 기부금 수령처)
        makePayoutCandidateItem(paymentDate, orderItem, PayoutEventType.정산__상품판매_기부금, buyer, donation, donationAmount);
    }

    /**
     * 정산 후보 아이템 생성을 위한 DTO를 받아 실제 엔티티를 생성하는 헬퍼 메서드
     */
    private void makePayoutCandidateItem(
            LocalDateTime paymentDate,
            OrderPayoutDto orderItem,
            PayoutEventType eventType,
            PayoutUser payer,
            PayoutSeller payee,
            BigDecimal amount
    ) {
        makePayoutCandidateItem(CreatePayoutCandidateItemDto.builder()
                .eventType(eventType)
                .relTypeCode("OrderDetail")
                .relId(orderItem.id())
                .paymentDate(paymentDate)
                .payer(payer)
                .payee(payee)
                .amount(amount)
                .build());
    }

    /**
     * DTO를 PayoutCandidateItem 엔티티로 변환하고 저장
     */
    private void makePayoutCandidateItem(CreatePayoutCandidateItemDto dto) {

        PayoutCandidateItem payoutCandidateItem = PayoutCandidateItem.from(dto);
        payoutCandidateItemRepository.save(payoutCandidateItem);
    }
}
