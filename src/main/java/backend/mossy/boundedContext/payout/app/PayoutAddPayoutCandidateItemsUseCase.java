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
        marketApiClient.getOrderItems(order.id())
                .forEach(orderItem -> makePayoutCandidateItems(order, orderItem));
    }

    private void makePayoutCandidateItems(
            OrderDto order,
            OrderItemDto orderItem
    ) {
        PayoutSeller system = payoutSupport.findSystemSeller().get();
        PayoutSeller buyer = payoutSupport.findSellerById(orderItem.buyerId()).get();
        PayoutSeller seller = payoutSupport.findSellerById(orderItem.sellerId()).get();

        makePayoutCandidateItem(
                PayoutEventType.정산__상품판매_수수료,
                orderItem.modelTypeCode(),
                orderItem.id(),
                order.paymentDate(),
                buyer,
                system,
                orderItem.payoutFee()
        );

        makePayoutCandidateItem(
                PayoutEventType.정산__상품판매_대금,
                orderItem.modelTypeCode(),
                orderItem.id(),
                order.paymentDate(),
                buyer,
                seller,
                orderItem.salePriceWithoutFee()
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
