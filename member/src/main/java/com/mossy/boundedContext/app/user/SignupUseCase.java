package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.global.ut.EncryptionUtils;
import com.mossy.boundedContext.in.dto.request.SignupRequest;
import com.mossy.boundedContext.out.repository.user.RoleRepository;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.boundedContext.out.s3.S3Adapter;
import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.domain.role.Role;
import com.mossy.shared.member.domain.role.RoleCode;
import com.mossy.boundedContext.domain.role.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SignupUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;
    private final S3Adapter s3Adapter;

    @Transactional
    public User execute(SignupRequest req, MultipartFile profileImage) {

        userRepository.findByEmail(req.email()).ifPresent(existing -> {
            if (existing.getSocialAccounts() != null && !existing.getSocialAccounts().isEmpty()) {
                throw new DomainException(ErrorCode.SOCIAL_ACCOUNT_EXISTS);
            }
            throw new DomainException(ErrorCode.DUPLICATE_EMAIL);
        });

        if (userRepository.existsByNickname(req.nickname())) {
            throw new DomainException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 프로필 이미지 처리: 파일이 있으면 S3 업로드, 없으면 기본 이미지
        String profileImageUrl = "default-user";
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Adapter.uploadProfileImage(profileImage);
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
                .profileImage(profileImageUrl)
                .status(UserStatus.ACTIVE)
                .build();

        Role roleUser = roleRepository.findByCode(RoleCode.USER)
                .orElseThrow(() -> new DomainException(ErrorCode.ROLE_NOT_FOUND));

        user.addUserRole(new UserRole(user, roleUser));

        return userRepository.save(user);
    }
}