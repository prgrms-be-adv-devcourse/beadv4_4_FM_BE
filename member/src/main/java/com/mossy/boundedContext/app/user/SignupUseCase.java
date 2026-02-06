package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.in.dto.request.SignupRequest;
import com.mossy.boundedContext.out.user.RoleRepository;
import com.mossy.boundedContext.out.user.UserRepository;
import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.domain.role.Role;
import com.mossy.shared.member.domain.role.RoleCode;
import com.mossy.boundedContext.domain.role.UserRole;
import com.mossy.standard.ut.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;

    @Transactional
    public User execute(SignupRequest req) {

        if (userRepository.existsByEmail(req.email())) {
            throw new DomainException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(req.nickname())) {
            throw new DomainException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.builder()
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .name(req.name())
                .nickname(req.nickname())
                .phoneNum(encryptionUtils.encrypt(req.phoneNum()))
                .address(encryptionUtils.encrypt(req.address()))
                .rrnEncrypted(encryptionUtils.encrypt(req.rrn()))
                .longitude(req.longitude())
                .latitude(req.latitude())
                .profileImage("default.png")
                .status(UserStatus.ACTIVE)
                .build();

        Role roleUser = roleRepository.findByCode(RoleCode.USER)
                .orElseThrow(() -> new DomainException(ErrorCode.ROLE_NOT_FOUND));

        user.addUserRole(new UserRole(user, roleUser));

        return userRepository.save(user);
    }
}