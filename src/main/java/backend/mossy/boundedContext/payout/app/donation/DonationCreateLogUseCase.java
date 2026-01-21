package backend.mossy.boundedContext.payout.app.donation;

import backend.mossy.boundedContext.payout.app.payout.PayoutSupport;
import backend.mossy.boundedContext.payout.domain.PayoutUser;
import backend.mossy.boundedContext.payout.domain.donation.DonationCalculator;
import backend.mossy.boundedContext.payout.domain.donation.DonationLog;
import backend.mossy.boundedContext.payout.out.donation.DonationLogRepository;
import backend.mossy.shared.market.dto.event.OrderDto;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 기부 로그 생성 UseCase
 */
@Service
@RequiredArgsConstructor
public class DonationCreateLogUseCase {

    private final DonationCalculator donationCalculator;
    private final DonationLogRepository donationLogRepository;
    private final PayoutSupport payoutSupport;

    /**
     * 주문 아이템에 대한 기부 로그 생성
     * @param order 주문 정보
     * @param orderItem 주문 아이템 정보
     */
    public void createDonationLog(OrderDto order, OrderItemDto orderItem) {
        // 1. PayoutUser 조회
        PayoutUser user = payoutSupport.findUserById(orderItem.buyerId()).get();

        // 2. 기부금 계산
        BigDecimal donationAmount = donationCalculator.calculate(orderItem);

        // 3. 탄소 배출량 계산 (kg 단위)
        BigDecimal carbonKg = donationCalculator.getCarbon(orderItem);

        // 4. kg를 g로 변환
        Double carbonG = carbonKg.multiply(new BigDecimal("1000")).doubleValue();

        // 5. DonationLog 생성 및 저장
        DonationLog donationLog = DonationLog.builder()
                .user(user)
                .orderItemId(orderItem.id())
                .amount(donationAmount)
                .carbonOffsetG(carbonG)
                .build();

        donationLogRepository.save(donationLog);
    }
}
