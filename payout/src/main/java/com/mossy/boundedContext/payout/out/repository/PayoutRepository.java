package com.mossy.boundedContext.payout.out.repository;


import com.mossy.boundedContext.payout.app.common.PayoutCompletePayoutsMoreUseCase;
import com.mossy.boundedContext.payout.app.common.PayoutCreatePayoutUseCase;
import com.mossy.boundedContext.payout.domain.payout.Payout;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * [Output Adapter] {@link Payout} 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스
 * 정산 데이터의 저장, 조회, 업데이트 등의 기능을 담당합니다.
 */
public interface PayoutRepository extends JpaRepository<Payout, Long> {
    /**
     * 특정 수취인(Payee)에 대해 아직 정산일(payoutDate)이 지정되지 않은(NULL) 활성화된 Payout을 조회
     * 이 메서드는 주로 {@link PayoutCreatePayoutUseCase}나 {@link com.mossy.member.payout.app.payout.PayoutCollectPayoutItemsMoreUseCase}에서
     * 현재 진행 중인 Payout을 찾거나 생성하는 데 사용
     *
     * @param payee 정산 대상 수취인 {@link PayoutSeller} 객체
     * @return 조회된 Payout 객체 (Optional)
     */
    Optional<Payout> findByPayeeAndPayoutDateIsNull(PayoutSeller payee);

    /**
     * 정산 배치 중복 실행 시 동일 Payout에 동시에 addItem()이 호출되는 것을 방지하기 위해
     * 비관적 쓰기 락(PESSIMISTIC_WRITE)으로 활성화된 Payout을 조회
     *
     * @param payee 정산 대상 수취인
     * @return 락이 걸린 활성화된 Payout (Optional)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p FROM Payout p
            WHERE p.payee = :payee AND p.payoutDate IS NULL
            """)
    Optional<Payout> findByPayeeAndPayoutDateIsNullWithLock(@Param("payee") PayoutSeller payee);

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

    List<Payout> findByPayoutDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Payout> findByPayeeAndPayoutDateBetweenOrderByPayoutDateDesc(
            PayoutSeller payee, LocalDateTime from, LocalDateTime to);

    /**
     * 정산은 완료되었으나 아직 지급되지 않은 Payout들을 조회
     * 지급 배치에서 사용
     *
     * @param pageable 페이징 정보
     * @return 지급 대상 Payout 리스트
     */
    List<Payout> findByPayoutDateIsNotNullAndCreditDateIsNullOrderByIdAsc(Pageable pageable);

    /**
     * 지급 대상 Payout을 비관적 쓰기 락(PESSIMISTIC_WRITE)으로 조회
     * 배치 중복 실행 시 동일 Payout에 대한 중복 지갑 입금을 방지
     * lock timeout 3초 설정으로 다른 배치가 락 보유 중일 때 무한 대기 방지
     *
     * @param pageable 페이징 정보
     * @return 지급 대상 Payout 리스트 (락 획득)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query("""
            SELECT p FROM Payout p
            WHERE p.payoutDate IS NOT NULL AND p.creditDate IS NULL
            ORDER BY p.id ASC
            """)
    List<Payout> findCreditTargetsWithLock(Pageable pageable);
}
