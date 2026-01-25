package backend.mossy.boundedContext.payout.out.donation;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.boundedContext.payout.domain.donation.DonationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * [Output Adapter] {@link DonationLog} 엔티티에 대한 데이터 접근(저장, 조회 등)을 제공하는 Repository 인터페이스
 * Spring Data JPA의 기능을 활용하여 기본적인 CRUD 및 페이징 기능을 제공하며,
 * 도메인 특화된 쿼리 메서드를 정의
 */
public interface DonationLogRepository extends JpaRepository<DonationLog, Long> {

    /**
     * 아직 정산 처리되지 않은(isSettled가 false인) 모든 기부 로그를 조회
     * @return 미정산된 기부 로그 리스트
     */
    List<DonationLog> findByIsSettledFalse();

    /**
     * 특정 사용자(PayoutUser)가 생성한 모든 기부 로그를 조회
     * @param user 기부 로그를 조회할 PayoutUser 객체
     * @return 특정 사용자의 기부 로그 리스트
     */
    List<DonationLog> findByUser(PayoutUser user);

    /**
     * 특정 주문 아이템(orderItemId)과 관련된 모든 기부 로그를 조회
     * @param orderItemId 기부 로그를 조회할 주문 아이템 ID
     * @return 특정 주문 아이템의 기부 로그 리스트
     */
    List<DonationLog> findByOrderItemId(Long orderItemId);
}
