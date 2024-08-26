package com.example.member.Config;

//import com.example.member.User.Jwt.JwtAuthenticationFilter;
//import com.example.member.User.Jwt.TokenProvider;
import com.example.member.User.Jwt.JwtAuthenticationFilter;
import com.example.member.User.Jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    private final TokenProvider tokenProvider;

    // 기본 시큐리티 설정
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**","/find-id","/register","/login", "/refresh", "/reset-password").permitAll()// 해당 경로는 모두 접근 가능
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
