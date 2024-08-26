package com.example.member.User.Jwt;

import com.example.member.User.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@Transactional
public class CustomUserDetails implements UserDetails  {


    private final User user;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // userRole을 권한으로 간주하여 GrantedAuthority로 변환
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserRole()));
    }

    @Override
    public String getPassword() {
        return user.getUserPw();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
