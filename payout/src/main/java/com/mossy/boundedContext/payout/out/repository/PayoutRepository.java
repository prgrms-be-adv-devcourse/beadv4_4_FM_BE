package com.mossy.boundedContext.payout.out.repository;


import com.mossy.boundedContext.payout.app.common.PayoutCompletePayoutsMoreUseCase;
import com.mossy.boundedContext.payout.app.common.PayoutCreatePayoutUseCase;
import com.mossy.boundedContext.payout.domain.payout.Payout;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

    /**
     * 정산은 완료되었으나 아직 지급되지 않은 Payout들을 조회
     * 지급 배치에서 사용
     *
     * @param pageable 페이징 정보
     * @return 지급 대상 Payout 리스트
     */
    List<Payout> findByPayoutDateIsNotNullAndCreditDateIsNullOrderByIdAsc(Pageable pageable);
}
