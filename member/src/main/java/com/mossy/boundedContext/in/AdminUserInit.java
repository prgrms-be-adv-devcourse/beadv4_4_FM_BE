package com.mossy.boundedContext.in;

import com.mossy.boundedContext.domain.role.UserRole;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.out.repository.user.RoleRepository;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.domain.role.Role;
import com.mossy.shared.member.domain.role.RoleCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class AdminUserInit {

    @Bean
    @Transactional
    public CommandLineRunner initAdminUsers(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // admin 유저 초기화
            createAdminUserIfNotExists(userRepository, roleRepository, passwordEncoder,
                    "admin@mossy.com", "admin", "관리자");
        };
    }

    private void createAdminUserIfNotExists(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String password,
            String name) {

        // 이미 존재하면 스킵
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        // 관리자 Role 조회
        Role adminRole = roleRepository.findByCode(RoleCode.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        // Admin 사용자 생성
        User adminUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .nickname(name)
                .phoneNum("")
                .address("")
                .rrnEncrypted("")
                .profileImage("default-user")
                .status(UserStatus.ACTIVE)
                .longitude(BigDecimal.ZERO)
                .latitude(BigDecimal.ZERO)
                .build();

        // Admin Role 할당
        adminUser.addUserRole(new UserRole(adminUser, adminRole));

        // 저장
        userRepository.save(adminUser);
    }
}

