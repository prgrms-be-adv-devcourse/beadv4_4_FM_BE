package com.mossy.boundedContext.out.repository.user;

import com.mossy.boundedContext.domain.user.UserSocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, Long> {

    // provider + providerId로 소셜 계정 조회 (로그인 시 사용)
    Optional<UserSocialAccount> findByProviderAndProviderId(String provider, String providerId);

    // user + provider 조합으로 이미 연동된 소셜 계정인지 확인
    boolean existsByUserIdAndProvider(Long userId, String provider);
}

