package com.mossy.boundedContext.global.security;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.out.user.UserRepository;
import com.mossy.global.config.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(()  -> new DomainException(ErrorCode.USER_NOT_FOUND));

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getName(),
                user.getUserRoles().stream()
                        .map(r -> r.getRole().getCode().name()).toList(),
                null,
                true
        );
    }

    @Cacheable(value = "USER_DETAILS", key = "#userId")
    @Transactional(readOnly = true)
    public UserDetailsImpl loadUserById(Long userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(()  -> new DomainException(ErrorCode.USER_NOT_FOUND));

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getName(),
                user.getUserRoles().stream()
                        .map(r -> r.getRole().getCode().name()).toList(),
                null,
                true
        );
    }
}
