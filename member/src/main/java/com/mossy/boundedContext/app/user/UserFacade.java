package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.in.dto.request.SignupRequest;
import com.mossy.boundedContext.out.user.UserRepository;
import com.mossy.global.eventPublisher.EventPublisher;


import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.shared.member.payload.UserDtoMapper;
import com.mossy.shared.member.payload.UserPayload;
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

        UserPayload userPayload = UserDtoMapper.from(saved);
        eventPublisher.publish(new UserJoinedEvent (userPayload));
        log.info("UserJoinedEvent 발행 완료: userId ={}", user.getId());

        return saved.getId();


    }
}