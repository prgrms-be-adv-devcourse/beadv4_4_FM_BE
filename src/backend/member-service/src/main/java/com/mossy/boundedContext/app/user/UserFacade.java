package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.User;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.member.dto.event.UserDto;
import com.mossy.shared.member.dto.event.UserDtoMapper;
import com.mossy.shared.member.dto.request.SignupRequest;
import com.mossy.boundedContext.out.user.UserRepository;

import com.mossy.shared.member.event.UserJoinedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return saved.getId();


    }
}