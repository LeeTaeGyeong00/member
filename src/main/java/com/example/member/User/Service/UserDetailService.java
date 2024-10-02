package com.example.member.User.Service;

import com.example.member.User.Entity.User;
import com.example.member.User.Jwt.CustomUserDetails;
import com.example.member.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailService.class);

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        log.debug("UserRepository is null: {}", userRepository == null);
        System.out.println(username);
//        System.out.println(userRepository == null);
        return userRepository.findByUserEmail(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + username));
    }


}
