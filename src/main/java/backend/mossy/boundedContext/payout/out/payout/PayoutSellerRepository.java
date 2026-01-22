package backend.mossy.boundedContext.payout.out.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * [Output Adapter] {@link PayoutSeller} 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스
 * Payout 컨텍스트 내에서 판매자 정보를 조회, 저장, 업데이트하는 기능을 담당
 */
public interface PayoutSellerRepository extends JpaRepository<PayoutSeller, Long> {
    /**
     * 상점 이름(storeName)을 기준으로 PayoutSeller 엔티티를 조회합니다.
     * 이 메서드는 특히 "system" 또는 "donation"과 같은 특정 시스템 판매자를 찾을 때 유용하게 사용
     *
     * @param storeName 조회할 상점 이름
     * @return 조회된 PayoutSeller 객체 (Optional)
     */
    Optional<PayoutSeller> findByStoreName(String storeName);
}
