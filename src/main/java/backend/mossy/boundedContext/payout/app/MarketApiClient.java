package backend.mossy.boundedContext.payout.app;

import backend.mossy.shared.market.dto.event.OrderItemDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
                        100L,                         // buyerId
                        "구매자상점",                    // buyerName
                        200L,                         // sellerId
                        "판매자상점1",                   // sellerName
                        1001L,                        // productId
                        "테스트 상품1",                  // productName
                        new BigDecimal("10000"),      // price (원가)
                        new BigDecimal("10000"),      // salePrice (판매가)
                        new BigDecimal("0.1"),        // payoutRate (수수료율 10%)
                        new BigDecimal("1000"),       // payoutFee (수수료)
                        new BigDecimal("9000")        // salePriceWithoutFee (판매가 - 수수료)
                ),
                new OrderItemDto(
                        2L,                           // id
                        LocalDateTime.now(),          // createdAt
                        LocalDateTime.now(),          // updatedAt
                        orderId,                      // orderId
                        100L,                         // buyerId
                        "구매자상점",                    // buyerName
                        300L,                         // sellerId (다른 판매자)
                        "판매자상점2",                   // sellerName
                        1002L,                        // productId
                        "테스트 상품2",                  // productName
                        new BigDecimal("5000"),       // price (원가)
                        new BigDecimal("5000"),       // salePrice (판매가)
                        new BigDecimal("0.1"),        // payoutRate (수수료율 10%)
                        new BigDecimal("500"),        // payoutFee (수수료)
                        new BigDecimal("4500")        // salePriceWithoutFee (판매가 - 수수료)
                )
        );
    }
}
