package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.in.dto.request.OAuth2UserDTO;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.out.external.dto.response.SocialLonginResponse;
import com.mossy.boundedContext.out.repository.user.RoleRepository;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.domain.role.RoleCode;
import com.mossy.shared.member.domain.role.Role;
import com.mossy.boundedContext.domain.role.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessSocialLoginUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public SocialLonginResponse execute(OAuth2UserDTO userDTO) {
        log.info("소셜 로그인 처리: provider={}, email={}", userDTO.provider(), userDTO.email());

        // 이미 존재하는 사용자인 경우
        User user = userRepository.findByEmail(userDTO.email())
                .orElseGet(() -> createNewSocialUser(userDTO));

        return SocialLonginResponse.from(user);
    }


    //새로운 소셜 사용자 생성
    private User createNewSocialUser(OAuth2UserDTO userDTO) {
        log.info("새로운 소셜 사용자 생성: provider={}, email={}", userDTO.provider(), userDTO.email());

        // 닉네임 자동 생성 (provider + providerId의 일부)
        String nickname = generateUniqueNickname();

        User user = User.builder()
                .email(userDTO.email())
                .name(userDTO.name() != null ? userDTO.name() : "사용자")
                .nickname(nickname)
                .password("") // 소셜 로그인 사용자는 비밀번호 없음
                .phoneNum("") // 소셜 로그인으로부터 받지 않음
                .address("") // 소셜 로그인으로부터 받지 않음
                .rrnEncrypted("") // 소셜 로그인으로부터 받지 않음
                .profileImage("default.png")
                .status(UserStatus.ACTIVE)
                .longitude(BigDecimal.ZERO)
                .latitude(BigDecimal.ZERO)
                .build();

        Role roleUser = roleRepository.findByCode(RoleCode.USER)
                .orElseThrow(() -> new DomainException(ErrorCode.ROLE_NOT_FOUND));

        user.addUserRole(new UserRole(user, roleUser));

        User savedUser = userRepository.save(user);
        log.info("소셜 사용자 생성 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        return savedUser;
    }

    //고유한 닉네임 생성
    private String generateUniqueNickname() {
        String nickname;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            // UUID의 처음 8글자 + 랜덤 숫자
            nickname = "user_" + UUID.randomUUID().toString().substring(0, 8);
            attempts++;
        } while (userRepository.existsByNickname(nickname) && attempts < maxAttempts);

        if (attempts >= maxAttempts) {
            throw new RuntimeException("고유한 닉네임을 생성할 수 없습니다.");
        }

        return nickname;
    }
}


