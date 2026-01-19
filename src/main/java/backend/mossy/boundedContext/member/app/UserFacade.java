package backend.mossy.boundedContext.member.app;

import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.shared.member.dto.SignupRequest;
import backend.mossy.boundedContext.member.out.RoleRepository;
import backend.mossy.boundedContext.member.out.UserRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.domain.role.Role;
import backend.mossy.shared.member.domain.role.RoleCode;
import backend.mossy.shared.member.domain.role.UserRole;
import backend.mossy.shared.member.domain.user.UserStatus;
import backend.mossy.standard.ut.EncryptionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;

    @Transactional
    public Long signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new DomainException(ErrorCode.DUPLICATE_EMAIL);
        }
        if(userRepository.existsByNickname(req.nickname())) {
            throw new DomainException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 암호화
        String encodedPwd = passwordEncoder.encode(req.password());
        String encryptedRrn = encryptionUtils.encrypt(req.rrn());
        String encryptedPhone = encryptionUtils.encrypt(req.phoneNum());
        String encrytedAddress = encryptionUtils.encrypt(req.address());

        User user = User.builder()
                .email(req.email())
                .password(encodedPwd)
                .name(req.name())
                .nickname(req.nickname())
                .phoneNum(encryptedPhone)
                .address(encrytedAddress)
                .rrnEncrypted(encryptedRrn)
                .profileImage("default.png")
                .status(UserStatus.ACTIVE)
                .build();

        Role roleUser =roleRepository.findByCode(RoleCode.USER)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        UserRole userRole = new UserRole(user, roleUser);

        user.addUserRole(userRole);

        return userRepository.save(user).getId();
    }
}
