package backend.mossy.boundedContext.payout.out.donation;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.boundedContext.payout.domain.donation.DonationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationLogRepository extends JpaRepository<DonationLog, Long> {

    /**
     * 미정산된 기부 로그 조회
     */
    List<DonationLog> findByIsSettledFalse();

    /**
     * 특정 사용자의 기부 로그 조회
     */
    List<DonationLog> findByUser(PayoutUser user);

    /**
     * 특정 주문 아이템의 기부 로그 조회
     */
    List<DonationLog> findByOrderItemId(Long orderItemId);
}
