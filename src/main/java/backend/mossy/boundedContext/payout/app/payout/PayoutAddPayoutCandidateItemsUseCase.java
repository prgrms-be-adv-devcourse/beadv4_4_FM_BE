package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.PayoutUser;
import backend.mossy.boundedContext.payout.domain.donation.DonationCalculator;
import backend.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.payout.PayoutEventType;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.shared.payout.dto.event.CreatePayoutCandidateItemDto;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
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
    private final DonationCalculator donationCalculator;

    public void addPayoutCandidateItems(OrderDto order) {
        marketApiClient.getOrderItems(order.id())
                .forEach(orderItem -> makePayoutCandidateItems(order, orderItem));
    }

    private void makePayoutCandidateItems(
            OrderDto order,
            OrderItemDto orderItem
    ) {
        PayoutSeller system = payoutSupport.findSystemSeller().get();
        PayoutSeller donation = payoutSupport.findDonationSeller().get();
        PayoutUser buyer = payoutSupport.findUserById(orderItem.buyerId()).get();
        PayoutSeller seller = payoutSupport.findSellerById(orderItem.sellerId()).get();

        // 1. 기부금 계산
        BigDecimal donationAmount = donationCalculator.calculate(orderItem);

        // 2. 조정된 수수료 = 원래 수수료 - 기부금
        BigDecimal adjustedFee = orderItem.payoutFee().subtract(donationAmount);

        // 3. 정산 후보 항목 생성
        makePayoutCandidateItem(order, orderItem, PayoutEventType.정산__상품판매_수수료, buyer, system, adjustedFee);
        makePayoutCandidateItem(order, orderItem, PayoutEventType.정산__상품판매_대금, buyer, seller, orderItem.salePriceWithoutFee());
        makePayoutCandidateItem(order, orderItem, PayoutEventType.정산__상품판매_기부금, buyer, donation, donationAmount);
    }

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

    private void makePayoutCandidateItem(CreatePayoutCandidateItemDto dto) {
        PayoutCandidateItem payoutCandidateItem = PayoutCandidateItem.from(dto);
        payoutCandidateItemRepository.save(payoutCandidateItem);
    }
}
