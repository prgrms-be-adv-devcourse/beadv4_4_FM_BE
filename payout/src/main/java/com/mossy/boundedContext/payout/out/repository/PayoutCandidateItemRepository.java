package com.mossy.boundedContext.payout.out.repository;

import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.domain.payout.PayoutItem;
import com.mossy.shared.payout.enums.PayoutEventType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * 정산 배치 중복 실행 시 동일 CandidateItem을 두 배치가 동시에 처리하는 것을 방지하기 위해
     * 비관적 쓰기 락(PESSIMISTIC_WRITE)으로 정산 준비 후보를 조회
     *
     * @param before   결제 발생일 기준 (안전 대기 기간 경과된 항목만 조회)
     * @param pageable 페이징 정보
     * @return 락이 걸린 정산 준비 후보 리스트
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT c FROM PayoutCandidateItem c
            WHERE c.payoutItem IS NULL AND c.paymentDate < :before
            ORDER BY c.payee ASC, c.id ASC
            """)
    List<PayoutCandidateItem> findPayoutReadyWithLock(
            @Param("before") LocalDateTime before,
            Pageable pageable
    );

    /**
     * 특정 Payout에 포함된 PayoutItem 중 특정 이벤트 타입의 PayoutCandidateItem들을 조회
     * 정산 완료 시 기부금 관련 항목들을 찾아 기부 로그를 생성하기 위해 사용
     *
     * @param payoutId Payout ID
     * @param eventType 조회할 이벤트 타입 (예: 정산__상품판매_기부금)
     * @return 조회 조건에 맞는 정산 후보 아이템 리스트
     */
    List<PayoutCandidateItem> findByPayoutItem_Payout_IdAndEventType(Long payoutId, PayoutEventType eventType);

    List<PayoutCandidateItem> findByRelIdAndRelTypeCode(Long relId, String relTypeCode);

    /**
     * Kafka at-least-once 재처리 시 동일 OrderItem에 대한 중복 생성 여부를 확인
     * 대표 이벤트 타입(정산__상품판매_대금)으로 존재 여부를 확인하여 이미 처리된 경우 skip
     *
     * @param relTypeCode 관련 엔티티 타입 코드 (예: "OrderItem")
     * @param relId       관련 엔티티 ID (예: OrderItem ID)
     * @param eventType   이벤트 타입 (대표값으로 정산__상품판매_대금 사용)
     * @return 이미 처리된 경우 true
     */
    boolean existsByRelTypeCodeAndRelIdAndEventType(
            String relTypeCode, Long relId, PayoutEventType eventType
    );

    /**
     * 환불 처리 시 동일 orderItem에 대한 중복 환불을 방지하기 위해
     * 비관적 쓰기 락(PESSIMISTIC_WRITE)으로 정산 후보를 조회
     * 락 보유 중 다른 트랜잭션은 해당 row를 읽거나 수정할 수 없음
     *
     * @param relId        관련 엔티티 ID (예: OrderItem ID)
     * @param relTypeCode  관련 엔티티 타입 코드 (예: "OrderItem")
     * @return 락이 걸린 정산 후보 리스트
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT c FROM PayoutCandidateItem c
            WHERE c.relId = :relId AND c.relTypeCode = :relTypeCode
            """)
    List<PayoutCandidateItem> findByRelIdAndRelTypeCodeWithLock(
            @Param("relId") Long relId,
            @Param("relTypeCode") String relTypeCode
    );
}
