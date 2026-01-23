package backend.mossy.boundedContext.member.app.user;

import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.member.dto.event.UserDto;
import backend.mossy.shared.member.dto.request.SignupRequest;
import backend.mossy.boundedContext.member.out.user.UserRepository;
import backend.mossy.shared.member.event.UserDtoMapper;
import backend.mossy.shared.member.event.UserJoinedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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