package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.PayoutEventType;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.boundedContext.payout.out.PayoutCandidateItemRepository;
import backend.mossy.shared.market.dto.response.OrderItemResponseDto;
import backend.mossy.shared.market.dto.response.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayoutAddPayoutCandidateItemsUseCase {
    private final MarketApiClient marketApiClient;
    private final PayoutSupport payoutSupport;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;

    public void addPayoutCandidateItems(OrderItemResponseDto order) {
        // MarketApiClient를 통해 주문에 속한 개별 상품(OrderItem) 목록을 가져옵니다.
        marketApiClient.getOrderItemResponseDto(order.getId())
                // 각 상품별로 정산 후보 데이터를 생성합니다.
                .forEach(orderItem -> makePayoutCandidateItems(order, orderItem));
    }

    private void makePayoutCandidateItems(
            OrderResponseDto order,
            OrderItemResponseDto orderItem
    ) {

        PayoutSeller system = payoutSupport.findSystemMember().get();
        PayoutSeller buyer = payoutSupport.findMemberById(orderItem.getBuyerId()).get();
        PayoutSeller seller = payoutSupport.findMemberById(orderItem.getSellerId()).get();
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
            long amount
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

        // 생성된 정산 후보 데이터를 DB에 저장합니다.
        // 이 데이터는 나중에 배치 작업에 의해 처리될 때까지 대기 상태가 됩니다.
        payoutCandidateItemRepository.save(payoutCandidateItem);
    }
}
