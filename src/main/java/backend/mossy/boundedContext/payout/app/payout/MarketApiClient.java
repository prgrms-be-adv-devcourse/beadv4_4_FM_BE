package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.*;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Market Bounded Context의 API를 호출하기 위한 클라이언트입니다.
 * <p>
 * TODO: 현재는 테스트를 위한 Mock 데이터로 구현되어 있으며, 향후 실제 Market 서비스와 통신하는 로직(예: FeignClient)으로 교체되어야 합니다.
 *       (현재 담당자 구현 지연으로 인해 임시적으로 Mock으로 구현됨)
 * <p>
 * 참고: 정산 흐름 2단계에 해당하는 PayoutCollectPayoutItemsMoreUseCase가 내부 클래스로 포함되어 있습니다.
 *      이는 Payout 로직이 Market 데이터에 강하게 의존하고 있음을 보여주며, 향후 리팩토링을 통해 분리하는 것을 고려할 수 있습니다.
 */
@Component
public class MarketApiClient {

    /**
     * [Mock] 특정 주문 ID에 해당하는 주문 아이템 목록을 반환합니다.
     *
     * TODO: 실제 Market Bounded Context의 API를 호출하여 데이터를 가져오도록 구현 필요
     *
     * 실제 구현 방법:
     * 1. Market의 OrderFacade에 getOrderItems(Long orderId) 메서드 추가
     * 2. Order 조회 시 orderDetails를 join fetch로 함께 조회
     * 3. OrderDetail에서 필요한 정보 추출:
     *    - OrderDetail의 id, orderId, quantity, orderPrice
     *    - Order의 buyer 정보 (id, name)
     *    - OrderDetail의 seller 정보 (id, storeName)
     *    - OrderDetail의 productId로 Product 조회하여 name, price, weight
     *    - OrderDetail의 weightGrade, deliveryDistance
     * 4. 수수료 계산: payoutRate = 0.1 (10%), payoutFee = salePrice * 0.1
     * 5. OrderItemDto로 변환하여 반환
     *
     * 참고: 현재는 Market 담당자 구현 지연으로 임시 Mock 데이터 사용 중
     *
     * @param orderId 주문 ID
     * @return 주문 아이템 DTO 리스트 (현재는 하드코딩된 테스트 데이터)
     */
    public List<OrderItemDto> getOrderItems(Long orderId) {
        // TODO: 실제 구현 예시
        // return marketOrderFacade.getOrderItems(orderId);

        // 임시 Mock 데이터 - 각 탄소 등급(A, B, C, D)별 테스트
        return List.of(
                // A등급 테스트: 0~10kg 탄소, 10% 기부
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
                        "A등급상품(저탄소)",              // productName
                        new BigDecimal("10000"),      // price (원가)
                        new BigDecimal("10000"),      // salePrice (판매가)
                        new BigDecimal("0.1"),        // payoutRate (수수료율 10%)
                        new BigDecimal("1000"),       // payoutFee (수수료 1000원)
                        new BigDecimal("9000"),       // salePriceWithoutFee
                        new BigDecimal("0.5"),        // weight (0.5kg)
                        new BigDecimal("10")          // deliveryDistance (10km) -> 탄소 5kg (A등급, 10% 기부 = 100원)
                ),
                // B등급 테스트: 10~30kg 탄소, 20% 기부
                new OrderItemDto(
                        2L,                           // id
                        LocalDateTime.now(),          // createdAt
                        LocalDateTime.now(),          // updatedAt
                        orderId,                      // orderId
                        1000L,                        // buyerId (User ID)
                        "김구매",                       // buyerName
                        200L,                         // sellerId
                        "판매자상점1",                   // sellerName
                        1002L,                        // productId
                        "B등급상품(중저탄소)",            // productName
                        new BigDecimal("20000"),      // price (원가)
                        new BigDecimal("20000"),      // salePrice (판매가)
                        new BigDecimal("0.1"),        // payoutRate (수수료율 10%)
                        new BigDecimal("2000"),       // payoutFee (수수료 2000원)
                        new BigDecimal("18000"),      // salePriceWithoutFee
                        new BigDecimal("2"),          // weight (2kg)
                        new BigDecimal("10")          // deliveryDistance (10km) -> 탄소 20kg (B등급, 20% 기부 = 400원)
                ),
                // C등급 테스트: 30~50kg 탄소, 30% 기부
                new OrderItemDto(
                        3L,                           // id
                        LocalDateTime.now(),          // createdAt
                        LocalDateTime.now(),          // updatedAt
                        orderId,                      // orderId
                        1000L,                        // buyerId (User ID)
                        "김구매",                       // buyerName
                        300L,                         // sellerId (다른 판매자)
                        "판매자상점2",                   // sellerName
                        1003L,                        // productId
                        "C등급상품(중탄소)",              // productName
                        new BigDecimal("30000"),      // price (원가)
                        new BigDecimal("30000"),      // salePrice (판매가)
                        new BigDecimal("0.1"),        // payoutRate (수수료율 10%)
                        new BigDecimal("3000"),       // payoutFee (수수료 3000원)
                        new BigDecimal("27000"),      // salePriceWithoutFee
                        new BigDecimal("5"),          // weight (5kg)
                        new BigDecimal("8")           // deliveryDistance (8km) -> 탄소 40kg (C등급, 30% 기부 = 900원)
                ),
                // D등급 테스트: 50kg~ 탄소, 40% 기부
                new OrderItemDto(
                        4L,                           // id
                        LocalDateTime.now(),          // createdAt
                        LocalDateTime.now(),          // updatedAt
                        orderId,                      // orderId
                        1000L,                        // buyerId (User ID)
                        "김구매",                       // buyerName
                        300L,                         // sellerId (다른 판매자)
                        "판매자상점2",                   // sellerName
                        1004L,                        // productId
                        "D등급상품(고탄소)",              // productName
                        new BigDecimal("50000"),      // price (원가)
                        new BigDecimal("50000"),      // salePrice (판매가)
                        new BigDecimal("0.1"),        // payoutRate (수수료율 10%)
                        new BigDecimal("5000"),       // payoutFee (수수료 5000원)
                        new BigDecimal("45000"),      // salePriceWithoutFee
                        new BigDecimal("10"),         // weight (10kg)
                        new BigDecimal("10")          // deliveryDistance (10km) -> 탄소 100kg (D등급, 40% 기부 = 2000원)
                )
        );
    }

    /**
     * [UseCase] 정산 후보 아이템을 실제 정산 아이템으로 집계하는 서비스 클래스입니다.
     * PayoutFacade의 '2단계: 정산 아이템 집계' 흐름에 해당하며, 배치(Batch) 작업으로 실행되는 것을 가정합니다.
     */
    @Service
    @RequiredArgsConstructor
    public static class PayoutCollectPayoutItemsMoreUseCase {
        private final PayoutRepository payoutRepository;
        private final PayoutCandidateItemRepository payoutCandidateItemRepository;

        /**
         * 정산 대기 중인 후보 아이템들을 조회하여, 각 수취인(payee)별로 아직 완료되지 않은 정산(Payout)에 추가합니다.
         *
         * @param limit 한 번의 배치 작업에서 처리할 최대 아이템 수
         * @return 처리 결과 RsData
         */
        @Transactional
        public RsData<Integer> collectPayoutItemsMore(int limit) {

            // 1. 정산 처리 대기일이 지난 후보 아이템들을 조회합니다.
            List<PayoutCandidateItem> payoutReadyCandidateItems = findPayoutReadyCandidateItems(limit);

            // 2. 처리할 아이템이 없으면 작업을 종료합니다.
            if (payoutReadyCandidateItems.isEmpty())
                return new RsData<>("200-1", "더 이상 정산에 추가할 항목이 없습니다.", 0);

            // 3. 후보 아이템들을 수취인(payee) 기준으로 그룹핑합니다.
            payoutReadyCandidateItems.stream()
                    .collect(Collectors.groupingBy(PayoutCandidateItem::getPayee))
                    .forEach((payee, candidateItems) -> {

                        // 4. 각 수취인에 대해 현재 진행 중인(아직 정산일이 미지정된) Payout 객체를 찾습니다.
                        //    (없으면 새로 만들어야 하지만, 현재 로직에서는 기존 Payout을 찾는 것으로 보입니다.)
                        Payout payout = findActiveByPayee(payee).get();

                        // 5. 조회된 후보 아이템들을 Payout에 실제 정산 항목(PayoutItem)으로 추가합니다.
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
                            
                            // 6. 후보 아이템에 방금 생성된 정산 아이템을 연결하여, 중복 처리되지 않도록 표시합니다.
                            item.setPayoutItem(payoutItem);
                        });
                    });


            return new RsData<>(
                    "201-1",
                    "%d건의 정산데이터가 생성되었습니다.".formatted(payoutReadyCandidateItems.size()),
                    payoutReadyCandidateItems.size()
            );
        }

        /**
         * 특정 수취인의 아직 정산되지 않은(payoutDate == null) Payout을 조회합니다.
         */
        private Optional<Payout> findActiveByPayee(PayoutSeller payee) {
            return payoutRepository.findByPayeeAndPayoutDateIsNull(payee);
        }

        /**
         * 정산 대기 기간(PayoutPolicy.PAYOUT_READY_WAITING_DAYS)이 지난 정산 후보 아이템들을 조회합니다.
         * PayoutItem이 아직 연결되지 않은(아직 처리되지 않은) 아이템만 대상으로 합니다.
         *
         * @param limit 조회할 최대 아이템 수
         * @return 정산 준비가 된 후보 아이템 리스트
         */
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
