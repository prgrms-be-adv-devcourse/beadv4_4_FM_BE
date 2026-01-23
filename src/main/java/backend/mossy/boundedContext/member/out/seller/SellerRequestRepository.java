package backend.mossy.boundedContext.member.out.seller;

import backend.mossy.shared.member.domain.seller.SellerRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SellerRequestRepository extends JpaRepository<SellerRequest, Long> {

    boolean existsByActiveUserId(Long activeUserId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sr from SellerRequest sr where sr.id = :id")
    Optional<SellerRequest> findByIdForUpdate(@Param("id") Long id);
}
