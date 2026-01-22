package backend.mossy.boundedContext.payout.out.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.payout.PayoutItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * [Output Adapter] {@link PayoutCandidateItem} 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스
 * 정산 후보 아이템의 조회, 저장, 업데이트 등의 기능을 담당
 */
public interface PayoutCandidateItemRepository extends JpaRepository<PayoutCandidateItem, Long> {
    /**
     * 아직 {@link PayoutItem}으로 처리되지 않은(payoutItem이 null인) 정산 후보 아이템 중,
     * 특정 결제 발생일(paymentDate) 이전에 발생한 아이템들을 조회
     * 조회 결과는 수취인(payee)과 ID 기준으로 오름차순 정렬
     * 이 메서드는 주로 배치 처리 시 정산 대상 후보 아이템을 선택하는 데 사용
     *
     * @param paymentDate 조회 기준이 되는 결제 발생일 (이 날짜 이전의 아이템들이 조회됨)
     * @param pageable 페이징 및 제한(limit) 정보를 위한 Pageable 객체
     * @return 조회 조건에 맞는 정산 후보 아이템 리스트
     */
    List<PayoutCandidateItem> findByPayoutItemIsNullAndPaymentDateBeforeOrderByPayeeAscIdAsc(LocalDateTime paymentDate, Pageable pageable);
}
