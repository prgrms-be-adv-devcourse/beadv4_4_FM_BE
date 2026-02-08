package com.mossy.global.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final String nickname;
    private final String name;
    private final Long sellerId;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public UserDetailsImpl(
            Long userId,
            String email,
            String password,
            String nickname,
            String name,
            List<String> roles,
            Long sellerId,
            boolean enabled
    ) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.sellerId = sellerId;
        this.enabled = enabled;
        this.authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority>  getAuthorities() {
        return authorities;
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return enabled;
    }



}
