package com.mossy.boundedContext.cash.out.user;

import com.mossy.boundedContext.cash.domain.user.UserWallet;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

    boolean existsWalletByUserId(Long userId);

    Optional<UserWallet> findWalletByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from UserWallet w where w.user.id = :userId")
    Optional<UserWallet> findWalletByUserIdForUpdate(@Param("userId") Long userId);
}
