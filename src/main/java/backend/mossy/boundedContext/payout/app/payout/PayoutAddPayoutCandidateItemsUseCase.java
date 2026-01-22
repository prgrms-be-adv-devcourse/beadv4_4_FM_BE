package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.boundedContext.payout.domain.donation.DonationCalculator;
import backend.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.payout.PayoutEventType;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.shared.payout.dto.event.payout.CreatePayoutCandidateItemDto;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
import backend.mossy.shared.market.dto.event.OrderDto;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * [UseCase] 정산 후보 아이템 생성을 담당하는 서비스 클래스
 * PayoutFacade의 '1단계: 정산 후보 아이템 생성' 흐름에서 호출
 * 주문이 완료되었을 때, 해당 주문 정보를 바탕으로 미래에 정산될 항목들을 미리 생성
 */
@Service
@RequiredArgsConstructor
public class PayoutAddPayoutCandidateItemsUseCase {
    private final MarketApiClient marketApiClient;
    private final PayoutSupport payoutSupport;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final DonationCalculator donationCalculator;

    /**
     * 특정 주문(Order)에 포함된 모든 주문 아이템(OrderItem)에 대해 정산 후보 항목을 생성
     * @param order 완료된 주문 정보 DTO
     */
    @Transactional
    public void addPayoutCandidateItems(OrderDto order) {
        // MarketApiClient를 통해 주문에 속한 모든 주문 아이템을 가져와 각각 처리
        marketApiClient.getOrderItems(order.id())
                .forEach(orderItem -> makePayoutCandidateItems(order, orderItem));
    }

    /**
     * 하나의 주문 아이템(OrderItem)을 여러 개의 정산 후보 아이템(PayoutCandidateItem)으로 분해하여 생성
     * 예를 들어, 하나의 상품 판매는 (1)판매자에게 갈 판매대금, (2)플랫폼의 수수료, (3)기부금
     *
     * @param order 주문 정보
     * @param orderItem 처리할 개별 주문 아이템
     */
    private void makePayoutCandidateItems(
            OrderDto order,
            OrderItemDto orderItem
    ) {
        // --- 정산에 필요한 주요 주체(Actor)들을 조회 ---
        PayoutSeller system = payoutSupport.findSystemSeller().get(); // 시스템(플랫폼)
        PayoutSeller donation = payoutSupport.findDonationSeller().get(); // 기부금 수령처(가상 판매자)
        PayoutUser buyer = payoutSupport.findUserById(orderItem.buyerId()).get(); // 구매자
        PayoutSeller seller = payoutSupport.findSellerById(orderItem.sellerId()).get(); // 판매자

        // --- 금액을 계산 ---
        // 1. 이 상품 판매로 인해 발생한 기부금을 계산
        BigDecimal donationAmount = donationCalculator.calculate(orderItem);

        // 2. 조정된 수수료를 계산합니다. (원래 수수료 - 기부금)
        BigDecimal adjustedFee = orderItem.payoutFee().subtract(donationAmount);

        // --- 계산된 금액을 바탕으로 3가지 종류의 정산 후보 아이템을 생성 ---
        // 아이템 1: 플랫폼 수수료 (구매자 -> 시스템)
        makePayoutCandidateItem(order, orderItem, PayoutEventType.정산__상품판매_수수료, buyer, system, adjustedFee);
        // 아이템 2: 판매 대금 (구매자 -> 판매자)
        makePayoutCandidateItem(order, orderItem, PayoutEventType.정산__상품판매_대금, buyer, seller, orderItem.salePriceWithoutFee());
        // 아이템 3: 기부금 (구매자 -> 기부금 수령처)
        makePayoutCandidateItem(order, orderItem, PayoutEventType.정산__상품판매_기부금, buyer, donation, donationAmount);
    }

    /**
     * 정산 후보 아이템 생성을 위한 DTO를 받아 실제 엔티티를 생성하는 헬퍼 메서드
     */
    private void makePayoutCandidateItem(
            OrderDto order,
            OrderItemDto orderItem,
            PayoutEventType eventType,
            PayoutUser payer,
            PayoutSeller payee,
            BigDecimal amount
    ) {
        makePayoutCandidateItem(CreatePayoutCandidateItemDto.builder()
                .eventType(eventType)
                .relTypeCode(orderItem.getModelTypeCode())
                .relId(orderItem.id())
                .paymentDate(order.paymentDate())
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
