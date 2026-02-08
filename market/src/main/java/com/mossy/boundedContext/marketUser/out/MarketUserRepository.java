package com.mossy.boundedContext.marketUser.out;

import com.mossy.boundedContext.marketUser.domain.MarketUser;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MarketUserRepository extends JpaRepository<MarketUser, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from MarketUser u where u.id = :userId")
    Optional<MarketUser> findByIdWithLock(@Param("userId") Long userId);
}
