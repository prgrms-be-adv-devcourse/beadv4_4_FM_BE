package com.mossy.boundedContext.out.repository.seller;

import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerRequestRepository extends JpaRepository<SellerRequest, Long> {

    boolean existsByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sr from SellerRequest sr where sr.id = :id")
    Optional<SellerRequest> findByIdForUpdate(@Param("id") Long id);

    boolean existsByBusinessNum(String businessNum);

    Optional<SellerRequest> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    List<SellerRequest> findAllByStatus(SellerRequestStatus status);

    @Query("select sr from SellerRequest sr where sr.status = :status order by sr.createdAt desc")
    List<SellerRequest> findByStatus(@Param("status") SellerRequestStatus status);
}
