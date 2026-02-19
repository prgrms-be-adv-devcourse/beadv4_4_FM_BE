package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.in.dto.OAuth2UserDto;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.out.external.dto.response.SocialLonginResponse;
import com.mossy.boundedContext.out.repository.user.RoleRepository;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.member.domain.role.RoleCode;
import com.mossy.shared.member.domain.role.Role;
import com.mossy.boundedContext.domain.role.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessSocialLoginUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public SocialLonginResponse execute(OAuth2UserDto userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.email());

        if (existingUser.isPresent()) {
            return SocialLonginResponse.from(existingUser.get(), false);
        } else {
            User user = createNewSocialUser(userDTO);
            return SocialLonginResponse.from(user, true);
        }
    }

    //새로운 소셜 사용자 생성
    private User createNewSocialUser(OAuth2UserDto userDTO) {
        log.info("새로운 소셜 사용자 생성: provider={}, email={}", userDTO.provider(), userDTO.email());

        // User 엔티티의 팩토리 메서드를 사용하여 새로운 사용자 생성
        User user = User.createFromOAuth2(userDTO.email(), userDTO.name());

        // USER 역할 추가
        Role roleUser = roleRepository.findByCode(RoleCode.USER)
                .orElseThrow(() -> new DomainException(ErrorCode.ROLE_NOT_FOUND));

        user.addUserRole(new UserRole(user, roleUser));

        User savedUser = userRepository.save(user);
        log.info("소셜 사용자 생성 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        return savedUser;
    }
}


