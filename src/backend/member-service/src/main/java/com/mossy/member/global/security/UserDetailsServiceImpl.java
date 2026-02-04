package com.mossy.member.global.security;

import com.mossy.member.out.user.UserRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
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

        return new UserDetailsImpl(user);
    }

    @Cacheable(value = "USER_DETAILS", key = "#userId")
    @Transactional(readOnly = true)
    public UserDetailsImpl loadUserById(Long userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(()  -> new DomainException(ErrorCode.USER_NOT_FOUND));

        return new UserDetailsImpl(user);
    }
}
