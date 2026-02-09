package com.mossy.boundedContext.payout.out;

import com.mossy.boundedContext.payout.domain.PayoutItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * [Output Adapter] {@link PayoutItem} 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스
 * 집계 없이 정산 항목을 직접 저장/조회하는 데 사용
 */
public interface PayoutItemRepository extends JpaRepository<PayoutItem, Long> {
}
