package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.in.dto.OAuth2UserDto;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.domain.user.UserSocialAccount;
import com.mossy.boundedContext.app.mapper.UserMapper;
import com.mossy.boundedContext.out.external.dto.response.SocialLonginResponse;
import com.mossy.boundedContext.out.repository.user.RoleRepository;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.boundedContext.out.repository.user.UserSocialAccountRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.member.domain.role.RoleCode;
import com.mossy.shared.member.domain.role.Role;
import com.mossy.boundedContext.domain.role.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessSocialLoginUseCase {

    private final UserRepository userRepository;
    private final UserSocialAccountRepository socialAccountRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;

    @Transactional
    public SocialLonginResponse execute(OAuth2UserDto userDTO) {
        String provider = userDTO.provider();
        String providerId = userDTO.providerId();
        String email = userDTO.email();
        Long linkUserId = userDTO.linkUserId();

        if (email == null || email.isBlank()) {
            throw new DomainException(ErrorCode.INVALID_USER_DATA);
        }

        // 0. 명시적 연동 (linkUserId가 존재하는 경우)
        if (linkUserId != null) {
            log.info("기존 계정에 소셜 연동 요청: userId={}, provider={}", linkUserId, provider);
            
            // 이미 이 소셜 계정이 다른 유저에게 연결되어 있는지 확인
            Optional<UserSocialAccount> existingSocialAccount =
                    socialAccountRepository.findByProviderAndProviderId(provider, providerId);
                    
            if (existingSocialAccount.isPresent()) {
                if (!existingSocialAccount.get().getUser().getId().equals(linkUserId)) {
                    log.error("이미 다른 계정에 연동된 소셜 계정입니다: provider={}, providerId={}", provider, providerId);
                    throw new DomainException(ErrorCode.ALREADY_REGISTERED_EMAIL);
                }
                // 이미 현재 유저에 연동되어 있음
                User user = existingSocialAccount.get().getUser();
                return mapper.toSocialLoginResponse(user, extractRoleNames(user), false);
            }
            
            // 연동 수행
            User user = userRepository.findById(linkUserId)
                    .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));
            
            saveSocialAccount(user, provider, providerId, email);
            return mapper.toSocialLoginResponse(user, extractRoleNames(user), false);
        }

        // 1. 일반 로그인: 이미 연동된 소셜 계정이 있으면 → 해당 유저로 로그인
        Optional<UserSocialAccount> existingSocialAccount =
                socialAccountRepository.findByProviderAndProviderId(provider, providerId);

        if (existingSocialAccount.isPresent()) {
            User user = existingSocialAccount.get().getUser();
            // PENDING 유저(추가정보 미입력 후 뒤로가기) → isNewUser=true로 다시 추가정보 입력 안내
            boolean needsAdditionalInfo = user.isPending();
            log.info("기존 소셜 계정으로 로그인: userId={}, provider={}, pending={}", user.getId(), provider, needsAdditionalInfo);
            return mapper.toSocialLoginResponse(user, extractRoleNames(user), needsAdditionalInfo);
        }

        // 2. 일반 로그인: 동일 이메일 유저 조회 → 있으면 자동 연동 방지 (에러 발생)
        Optional<User> existingUser = userRepository.findByEmailWithRoles(email);
        if (existingUser.isPresent()) {
            log.error("로그인 시도 중 동일 이메일 발견, 자동 연동 방지: email={}", email);
            throw new DomainException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }

        // 3. 신규 유저 생성 + 소셜 계정 생성
        User newUser = createNewSocialUser(userDTO);
        return mapper.toSocialLoginResponse(newUser, extractRoleNames(newUser), true);
    }

    // 소셜 계정을 repository에 직접 저장 - 중복이면 저장하지 않음
    private void saveSocialAccount(User user, String provider, String providerId, String email) {
        // provider+providerId 기준 중복 방지 (unique 제약 위반 원천 차단)
        if (socialAccountRepository.findByProviderAndProviderId(provider, providerId).isPresent()) {
            log.warn("⚠️ 소셜 계정 이미 존재(provider+providerId) - 저장 생략: provider={}, providerId={}",
                    provider, providerId);
            return;
        }

        // user_id + provider 기준 중복 방지
        if (socialAccountRepository.existsByUserIdAndProvider(user.getId(), provider)) {
            log.warn("⚠️ 소셜 계정 이미 연동됨(userId+provider) - 저장 생략: userId={}, provider={}",
                    user.getId(), provider);
            return;
        }

        UserSocialAccount socialAccount = UserSocialAccount.builder()
                .user(user)
                .provider(provider)
                .providerId(providerId)
                .socialEmail(email)
                .build();
        socialAccountRepository.save(socialAccount);
        log.info("✅ 소셜 계정 저장 완료: userId={}, provider={}, providerId={}",
                user.getId(), provider, providerId);
    }

    // 신규 유저 생성 후 소셜 계정 저장
    private User createNewSocialUser(OAuth2UserDto userDTO) {
        log.info("신규 소셜 유저 생성 시작: provider={}, email={}", userDTO.provider(), userDTO.email());

        User user = User.createFromOAuth2(userDTO.email(), userDTO.name());

        Role roleUser = roleRepository.findByCode(RoleCode.USER)
                .orElseThrow(() -> new DomainException(ErrorCode.ROLE_NOT_FOUND));
        user.addUserRole(new UserRole(user, roleUser));

        // user 저장 (user_roles cascade로 함께 저장)
        User savedUser = userRepository.save(user);
        log.info("신규 유저 저장 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        // 소셜 계정은 별도로 직접 저장 (cascade 없이)
        saveSocialAccount(savedUser, userDTO.provider(), userDTO.providerId(), userDTO.email());

        log.info("✅ 신규 소셜 유저 생성 완료: userId={}, provider={}", savedUser.getId(), userDTO.provider());
        return savedUser;
    }

    private List<String> extractRoleNames(User user) {
        return user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode().name())
                .toList();
    }

    /**
     * 보상 트랜잭션 - auth에서 토큰 발급 실패 시 호출
     * isNewUser=true인 경우만 유저 자체를 삭제
     * isNewUser=false(기존 유저에 소셜 연동)인 경우는 추가된 소셜 계정만 삭제
     */
    @Transactional
    public void rollback(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            boolean isOnlySocialUser = user.getPassword() == null || user.getPassword().isBlank();

            if (isOnlySocialUser) {
                // 소셜 전용 신규 유저 → 소셜 계정 먼저 명시적 삭제 후 유저 삭제
                log.warn("소셜 로그인 보상 트랜잭션 - 신규 유저 삭제: userId={}", userId);
                socialAccountRepository.deleteAll(
                    socialAccountRepository.findAllByUserId(userId)
                );
                userRepository.delete(user);
            } else {
                // 기존 일반 유저에 소셜 연동된 경우 → 마지막으로 추가된 소셜 계정만 삭제
                log.warn("소셜 로그인 보상 트랜잭션 - 소셜 계정 연동 해제: userId={}", userId);
                socialAccountRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                        .ifPresent(socialAccountRepository::delete);
            }
        });
    }
}
