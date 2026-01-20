package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.PayoutEventType;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.shared.payout.dto.event.CreatePayoutCandidateItemDto;
import backend.mossy.boundedContext.payout.out.PayoutCandidateItemRepository;
import backend.mossy.shared.market.dto.event.OrderDto;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

        makePayoutCandidateItem(order, orderItem, PayoutEventType.정산__상품판매_수수료, buyer, system, orderItem.payoutFee());
        makePayoutCandidateItem(order, orderItem, PayoutEventType.정산__상품판매_대금, buyer, seller, orderItem.salePriceWithoutFee());
    }

    private void makePayoutCandidateItem(
            OrderDto order,
            OrderItemDto orderItem,
            PayoutEventType eventType,
            PayoutSeller payer,
            PayoutSeller payee,
            BigDecimal amount
    ) {
        makePayoutCandidateItem(CreatePayoutCandidateItemDto.builder()
                .eventType(eventType)
                .relTypeCode(orderItem.modelTypeCode())
                .relId(orderItem.id())
                .paymentDate(order.paymentDate())
                .payer(payer)
                .payee(payee)
                .amount(amount)
                .build());
    }

    private void makePayoutCandidateItem(CreatePayoutCandidateItemDto dto) {
        PayoutCandidateItem payoutCandidateItem = PayoutCandidateItem.from(dto);
        payoutCandidateItemRepository.save(payoutCandidateItem);
    }
}
