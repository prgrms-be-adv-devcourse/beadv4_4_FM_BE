package backend.mossy.boundedContext.member.app;

import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.shared.member.dto.request.SignupRequest;
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
    private final SignupUseCase signupUseCase;

    @Transactional
    public Long signup(SignupRequest req){
        User user = signupUseCase.execute(req);
        return userRepository.save(user).getId();
    }
}