package backend.mossy.boundedContext.member.app;

import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.member.dto.event.UserDto;
import backend.mossy.shared.member.dto.request.SignupRequest;
import backend.mossy.boundedContext.member.out.RoleRepository;
import backend.mossy.boundedContext.member.out.UserRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.domain.role.Role;
import backend.mossy.shared.member.domain.role.RoleCode;
import backend.mossy.shared.member.domain.role.UserRole;
import backend.mossy.shared.member.domain.user.UserStatus;
import backend.mossy.shared.member.event.UserDtoMapper;
import backend.mossy.shared.member.event.UserJoinedEvent;
import backend.mossy.standard.ut.EncryptionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserRepository userRepository;
    private final SignupUseCase signupUseCase;
    private final EventPublisher eventPublisher;

    @Transactional
    public Long signup(SignupRequest req){
        User user = signupUseCase.execute(req);
        User saved = userRepository.save(user);

        UserDto userDto = UserDtoMapper.from(saved);
        eventPublisher.publish(new UserJoinedEvent (userDto));
        log.info("UserJoinedEvent 발행 완료: userId ={}", user.getId());

        return userRepository.save(user).getId();


    }
}