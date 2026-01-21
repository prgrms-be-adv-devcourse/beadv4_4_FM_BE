package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.*;
import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.payout.PayoutItem;
import backend.mossy.boundedContext.payout.domain.payout.PayoutPolicy;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Market API 호출을 위한 클라이언트 (임시 구현)
 * TODO: 실제 Market 서비스와 통신하는 로직으로 교체 필요
 */
@Component
public class MarketApiClient {

    public List<OrderItemDto> getOrderItems(Long orderId) {
        // TODO: 실제 Market API 호출로 교체
        // 임시 테스트 데이터
        return List.of(
                new OrderItemDto(
                        1L,                           // id
                        LocalDateTime.now(),          // createdAt
                        LocalDateTime.now(),          // updatedAt
                        orderId,                      // orderId
                        1000L,                        // buyerId (User ID)
                        "김구매",                       // buyerName
                        200L,                         // sellerId
                        "판매자상점1",                   // sellerName
                        1001L,                        // productId
                        "테스트 상품1",                  // productName
                        new BigDecimal("10000"),      // price (원가)
                        new BigDecimal("10000"),      // salePrice (판매가)
                        new BigDecimal("0.1"),        // payoutRate (수수료율 10%)
                        new BigDecimal("1000"),       // payoutFee (수수료)
                        new BigDecimal("9000"),       // salePriceWithoutFee (판매가 - 수수료)
                        new BigDecimal("1"),          // weight (무게 1kg)
                        new BigDecimal("20")          // deliveryDistance (거리 20km) -> 탄소 20kg (B등급, 20% 기부)
                ),
                new OrderItemDto(
                        2L,                           // id
                        LocalDateTime.now(),          // createdAt
                        LocalDateTime.now(),          // updatedAt
                        orderId,                      // orderId
                        1000L,                        // buyerId (User ID)
                        "김구매",                       // buyerName
                        300L,                         // sellerId (다른 판매자)
                        "판매자상점2",                   // sellerName
                        1002L,                        // productId
                        "테스트 상품2",                  // productName
                        new BigDecimal("5000"),       // price (원가)
                        new BigDecimal("5000"),       // salePrice (판매가)
                        new BigDecimal("0.1"),        // payoutRate (수수료율 10%)
                        new BigDecimal("500"),        // payoutFee (수수료)
                        new BigDecimal("4500"),       // salePriceWithoutFee (판매가 - 수수료)
                        new BigDecimal("2"),          // weight (무게 2kg)
                        new BigDecimal("50")          // deliveryDistance (거리 50km) -> 탄소 100kg (D등급, 40% 기부)
                )
        );
    }

    @Service
    @RequiredArgsConstructor
    public static class PayoutCollectPayoutItemsMoreUseCase {
        private final PayoutRepository payoutRepository;
        private final PayoutCandidateItemRepository payoutCandidateItemRepository;

        public RsData<Integer> collectPayoutItemsMore(int limit) {

            List<PayoutCandidateItem> payoutReadyCandidateItems = findPayoutReadyCandidateItems(limit);

            if (payoutReadyCandidateItems.isEmpty())
                return new RsData<>("200-1", "더 이상 정산에 추가할 항목이 없습니다.", 0);

            payoutReadyCandidateItems.stream()
                    .collect(Collectors.groupingBy(PayoutCandidateItem::getPayee))
                    .forEach((payee, candidateItems) -> {

                        Payout payout = findActiveByPayee(payee).get();

                        candidateItems.forEach(item -> {
                            PayoutItem payoutItem = payout.addItem(
                                    item.getEventType(),
                                    item.getRelTypeCode(),
                                    item.getRelId(),
                                    item.getPaymentDate(),
                                    item.getPayer(),
                                    item.getPayee(),
                                    item.getAmount()
                            );

                            item.setPayoutItem(payoutItem);
                        });
                    });


            return new RsData<>(
                    "201-1",
                    "%d건의 정산데이터가 생성되었습니다.".formatted(payoutReadyCandidateItems.size()),
                    payoutReadyCandidateItems.size()
            );
        }

        private Optional<Payout> findActiveByPayee(PayoutSeller payee) {
            return payoutRepository.findByPayeeAndPayoutDateIsNull(payee);
        }

        private List<PayoutCandidateItem> findPayoutReadyCandidateItems(int limit) {
            LocalDateTime daysAgo = LocalDateTime
                    .now()
                    .minusDays(PayoutPolicy.PAYOUT_READY_WAITING_DAYS)
                    .toLocalDate()
                    .atStartOfDay();

            return payoutCandidateItemRepository.findByPayoutItemIsNullAndPaymentDateBeforeOrderByPayeeAscIdAsc(
                    daysAgo,
                    PageRequest.of(0, limit)
            );
        }
    }
}
