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

        if (email == null || email.isBlank()) {
            throw new DomainException(ErrorCode.INVALID_USER_DATA);
        }

        // 1. 이미 연동된 소셜 계정이 있으면 → 해당 유저로 로그인
        Optional<UserSocialAccount> existingSocialAccount =
                socialAccountRepository.findByProviderAndProviderId(provider, providerId);

        if (existingSocialAccount.isPresent()) {
            User user = existingSocialAccount.get().getUser();
            log.info("기존 소셜 계정으로 로그인: userId={}, provider={}", user.getId(), provider);
            return mapper.toSocialLoginResponse(user, extractRoleNames(user), false);
        }

        // 2. 동일 이메일 유저 조회 → 있으면 소셜 계정 연동
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            linkSocialAccount(user, provider, providerId, email);
            log.info("기존 유저에 소셜 계정 연동: userId={}, provider={}", user.getId(), provider);
            return mapper.toSocialLoginResponse(user, extractRoleNames(user), false);
        }

        // 3. 신규 유저 생성 + 소셜 계정 생성
        User newUser = createNewSocialUser(userDTO);
        return mapper.toSocialLoginResponse(newUser, extractRoleNames(newUser), true);
    }

    // 기존 유저에 소셜 계정을 연동
    private void linkSocialAccount(User user, String provider, String providerId, String email) {
        UserSocialAccount socialAccount = UserSocialAccount.builder()
                .user(user)
                .provider(provider)
                .providerId(providerId)
                .socialEmail(email)
                .build();
        user.addSocialAccount(socialAccount);
    }

    // 신규 유저 + 소셜 계정 생성
    private User createNewSocialUser(OAuth2UserDto userDTO) {
        log.info("신규 소셜 유저 생성: provider={}, email={}", userDTO.provider(), userDTO.email());

        User user = User.createFromOAuth2(userDTO.email(), userDTO.name());

        Role roleUser = roleRepository.findByCode(RoleCode.USER)
                .orElseThrow(() -> new DomainException(ErrorCode.ROLE_NOT_FOUND));
        user.addUserRole(new UserRole(user, roleUser));

        User savedUser = userRepository.save(user);

        // 소셜 계정 연동
        linkSocialAccount(savedUser, userDTO.provider(), userDTO.providerId(), userDTO.email());

        log.info("신규 소셜 유저 생성 완료: userId={}", savedUser.getId());
        return savedUser;
    }

    private List<String> extractRoleNames(User user) {
        return user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode().name())
                .toList();
    }
}
