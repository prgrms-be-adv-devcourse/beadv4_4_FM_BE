package backend.mossy.boundedContext.payout.app.donation;

import backend.mossy.boundedContext.payout.app.payout.PayoutSupport;
import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.boundedContext.payout.domain.donation.DonationCalculator;
import backend.mossy.boundedContext.payout.domain.donation.DonationLog;
import backend.mossy.boundedContext.payout.out.donation.DonationLogRepository;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import backend.mossy.global.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * [UseCase] 기부 로그 생성을 담당하는 서비스 클래스
 * DonationFacade의 '1단계: 기부 로그 생성' 흐름에서 호출
 */
@Service
@RequiredArgsConstructor
public class DonationCreateLogUseCase {

    private final DonationCalculator donationCalculator;
    private final DonationLogRepository donationLogRepository;
    private final PayoutSupport payoutSupport;

    /**
     * 특정 주문 아이템에 대한 기부 로그(DonationLog)를 생성하고 저장
     *
     * @param orderItem 기부금이 발생한 특정 주문 아이템 DTO
     */
    @Transactional
    public void createDonationLog(OrderItemDto orderItem) {
        if (orderItem == null) {
            throw new DomainException(ErrorCode.ORDERITEM_IS_NULL);
        }
        // 1. 기부자(구매자) 정보를 조회
        PayoutUser user = payoutSupport.findUserById(orderItem.buyerId())
                .orElseThrow(() -> new DomainException(ErrorCode.PAYOUT_USER_NOT_FOUND));

        // 2. 기부금액을 계산
        BigDecimal donationAmount = donationCalculator.calculate(orderItem);
        if (donationAmount == null || donationAmount.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_DONATION_AMOUNT);
        }

        // 3. 탄소 배출량을 kg 단위로 계산
        BigDecimal carbonKg = donationCalculator.getCarbon(orderItem);
        if (carbonKg == null || carbonKg.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_CARBON_AMOUNT);
        }

        // 4. 계산된 탄소 배출량을 kg에서 g으로 변환
        Double carbonG = carbonKg.multiply(new BigDecimal("1000")).doubleValue();

        // 5. 계산된 정보를 바탕으로 DonationLog 엔티티를 생성하고 저장
        DonationLog donationLog = DonationLog.builder()
                .user(user)
                .orderItemId(orderItem.id())
                .amount(donationAmount)
                .carbonOffsetG(carbonG)
                .build();

        donationLogRepository.save(donationLog);
    }
}
