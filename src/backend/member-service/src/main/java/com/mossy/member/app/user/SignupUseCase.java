package com.mossy.member.app.user;

import com.mossy.member.out.user.RoleRepository;
import com.mossy.member.out.user.UserRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.shared.member.domain.role.Role;
import com.mossy.shared.member.domain.role.RoleCode;
import com.mossy.shared.member.domain.role.UserRole;
import com.mossy.member.domain.user.UserStatus;
import com.mossy.shared.member.dto.request.SignupRequest;
import com.mossy.standard.ut.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;

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

        return user;
    }
}