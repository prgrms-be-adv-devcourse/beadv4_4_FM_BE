package backend.mossy.boundedContext.payout.out.payout;

import backend.mossy.boundedContext.payout.app.payout.PayoutCompletePayoutsMoreUseCase;
import backend.mossy.boundedContext.payout.app.payout.PayoutCreatePayoutUseCase;
import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * [Output Adapter] {@link Payout} 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스
 * 정산 데이터의 저장, 조회, 업데이트 등의 기능을 담당합니다.
 */
public interface PayoutRepository extends JpaRepository<Payout, Long> {
    /**
     * 특정 수취인(Payee)에 대해 아직 정산일(payoutDate)이 지정되지 않은(NULL) 활성화된 Payout을 조회
     * 이 메서드는 주로 {@link PayoutCreatePayoutUseCase}나 {@link PayoutCollectPayoutItemsMoreUseCase}에서
     * 현재 진행 중인 Payout을 찾거나 생성하는 데 사용
     *
     * @param payee 정산 대상 수취인 {@link PayoutSeller} 객체
     * @return 조회된 Payout 객체 (Optional)
     */
    Optional<Payout> findByPayeeAndPayoutDateIsNull(PayoutSeller payee);

    /**
     * 아직 정산일(payoutDate)이 지정되지 않았고(NULL), 총 금액(amount)이 0보다 큰 Payout들을 조회
     * 조회 결과는 ID 기준으로 오름차순 정렬
     * 이 메서드는 주로 {@link PayoutCompletePayoutsMoreUseCase}에서 정산 완료 처리가 필요한 Payout 목록을 가져오는 데 사용
     *
     * @param amount 최소 금액 (일반적으로 BigDecimal.ZERO)
     * @param pageable 페이징 및 제한(limit) 정보를 위한 Pageable 객체
     * @return 조회 조건에 맞는 Payout 리스트
     */
    List<Payout> findByPayoutDateIsNullAndAmountGreaterThanOrderByIdAsc(BigDecimal amount, Pageable pageable);
}
