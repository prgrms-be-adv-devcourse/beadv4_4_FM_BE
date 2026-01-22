package backend.mossy.boundedContext.auth.app;

import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.boundedContext.member.out.UserRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.domain.role.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginContext execute(String email, String password) {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new DomainException(ErrorCode.INVALID_CREDENTIALS)); // 에러코드 통일

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new DomainException(ErrorCode.INVALID_CREDENTIALS);
        }

        String role = extractRole(user);
        return new LoginContext(user.getId(), role);
    }

    private String extractRole(User user) {
        List<UserRole> userRoles = user.getUserRoles();
        if (userRoles == null || userRoles.isEmpty()) return "USER";
        return userRoles.get(0).getRole().getCode().name();
    }

    public record LoginContext(Long userId, String role) {}
}
