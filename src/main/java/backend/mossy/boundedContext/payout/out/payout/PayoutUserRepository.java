package backend.mossy.boundedContext.payout.out.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * [Output Adapter] {@link PayoutUser} 엔티티에 대한 데이터 접근을 제공하는 Repository 인터페이스
 * Payout 컨텍스트 내에서 사용자 정보를 조회, 저장, 업데이트하는 기능을 담당
 * 기본적인 CRUD 기능을 {@link JpaRepository}로부터 상속받아 사용
 */
public interface PayoutUserRepository extends JpaRepository<PayoutUser, Long> {
}
