package backend.mossy.boundedContext.member.app.user;

import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.boundedContext.member.out.user.RoleRepository;
import backend.mossy.boundedContext.member.out.user.UserRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.domain.role.Role;
import backend.mossy.shared.member.domain.role.RoleCode;
import backend.mossy.shared.member.domain.role.UserRole;
import backend.mossy.shared.member.domain.user.UserStatus;
import backend.mossy.shared.member.dto.request.SignupRequest;
import backend.mossy.standard.ut.EncryptionUtils;
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

        System.out.println(">>> DTO longitude = " + req.longitude());
        System.out.println(">>> DTO latitude  = " + req.latitude());

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

        System.out.println(">>> ENTITY longitude = " + user.getLongitude());
        System.out.println(">>> ENTITY latitude  = " + user.getLatitude());

        Role roleUser = roleRepository.findByCode(RoleCode.USER)
                .orElseThrow(() -> new DomainException(ErrorCode.ROLE_NOT_FOUND));

        user.addUserRole(new UserRole(user, roleUser));

        return user;
    }
}