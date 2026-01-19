package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.PayoutEventType;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.boundedContext.payout.out.PayoutCandidateItemRepository;
import backend.mossy.shared.market.dto.event.OrderDto;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayoutAddPayoutCandidateItemsUseCase {
    private final MarketApiClient marketApiClient;
    private final PayoutSupport payoutSupport;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;

    public void addPayoutCandidateItems(OrderDto order) {
        // MarketApiClient를 통해 주문에 속한 개별 상품(OrderItem) 목록을 가져옵니다.
        marketApiClient.getOrderItems(order.getId())
                // 각 상품별로 정산 후보 데이터를 생성합니다.
                .forEach(orderItem -> makePayoutCandidateItems(order, orderItem));
    }

    private void makePayoutCandidateItems(
            OrderDto order,
            OrderItemDto orderItem
    ) {
        // 정산에 필요한 주체들(시스템, 구매자, 판매자)의 정보를 조회합니다.
        PayoutSeller system = payoutSupport.findSystemSeller().get();
        PayoutSeller buyer = payoutSupport.findSellerById(orderItem.getBuyerId()).get();
        PayoutSeller seller = payoutSupport.findSellerById(orderItem.getSellerId()).get();

        makePayoutCandidateItem(
                PayoutEventType.정산__상품판매_수수료,
                orderItem.getModelTypeCode(),
                orderItem.getId(),
                order.getPaymentDate(),
                buyer,
                system,
                orderItem.getPayoutFee()
        );

        makePayoutCandidateItem(
                PayoutEventType.정산__상품판매_대금,
                orderItem.getModelTypeCode(),
                orderItem.getId(),
                order.getPaymentDate(),
                buyer,
                seller,
                orderItem.getSalePriceWithoutFee()
        );
    }

    private void makePayoutCandidateItem(
            PayoutEventType eventType,
            String relTypeCode,
            Long relId,
            LocalDateTime paymentDate,
            PayoutSeller payer,
            PayoutSeller payee,
            BigDecimal amount
    ) {
        PayoutCandidateItem payoutCandidateItem = new PayoutCandidateItem(
                eventType,
                relTypeCode,
                relId,
                paymentDate,
                payer,
                payee,
                amount
        );

        payoutCandidateItemRepository.save(payoutCandidateItem);
    }
}
