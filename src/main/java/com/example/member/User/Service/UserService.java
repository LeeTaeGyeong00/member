package com.example.member.User.Service;


import com.example.member.User.Dto.UserUpdateDTO;
import com.example.member.User.Entity.ExitUser;
import com.example.member.User.Entity.RefreshToken;
import com.example.member.User.Entity.User;
import com.example.member.User.Jwt.TokenProvider;
import com.example.member.User.Repository.ExitUserRepository;
import com.example.member.User.Repository.RefreshTokenRepository;
import com.example.member.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService  {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final EmailService emailService;
    private final ExitUserRepository exitUserRepository;


    @Transactional
    public User registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getUserPw());
        user.setUserPw(encodedPassword);

        user.setRegDate(LocalDate.now());
        user.setModDate(LocalDateTime.now());

        return userRepository.save(user);
    }

    public String login(String userId, String password) {
        User user = userRepository.findByUserEmail(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));
        boolean isExitUser = exitUserRepository.existsByUserId(userId);
        if (isExitUser) {
            throw new IllegalArgumentException("This account has been deactivated.");
        }

        if (!passwordEncoder.matches(password, user.getUserPw())) {
            throw new RuntimeException("Invalid credentials");
        }

        return tokenProvider.generateToken(userId);
    }
    public void saveRefreshToken(String userId, String refreshToken) {
        RefreshToken token = new RefreshToken(userId, refreshToken);
        refreshTokenRepository.save(token);
    }

    public String generateRefreshToken(String userId) {
        return tokenProvider.generateRefreshToken(userId);
    }

    public String refreshAccessToken(String refreshToken) {
        if (tokenProvider.validateToken(refreshToken)) {
            String userId = tokenProvider.getUserEmailFromJWT(refreshToken);
            return tokenProvider.generateToken(userId);
        } else {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    public User getUserDetails(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userEmail));
    }


    public String updateUser(UserUpdateDTO updateDTO, HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);

        User user = userRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + currentUserEmail));

        if (updateDTO.getUserEmail() != null) {
            user.setUserEmail(updateDTO.getUserEmail());
        }
        if (updateDTO.getUserPw() != null) {
            user.setUserPw(passwordEncoder.encode(updateDTO.getUserPw()));
        }
        if (updateDTO.getUserEmail() != null) {
            user.setUserEmail(updateDTO.getUserEmail());
        }
        if (updateDTO.getUserPH() != null) {
            user.setUserPH(updateDTO.getUserPH());
        }
        if (updateDTO.getUserAdr() != null) {
            user.setUserAdr(updateDTO.getUserAdr());
        }

        userRepository.save(user);

        String newAccessToken = tokenProvider.generateToken(user.getUserEmail());
        return newAccessToken;
    }

    public void removeRefreshToken(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public void findIdByNameAndEmail(String name, String email) {

        Optional<User> user = userRepository.findByUserNameAndUserEmail(name, email);

        if (user.isPresent()) {
            String userId = user.get().getUserEmail();
            emailService.sendEmail(email, "Your User ID", "Your User ID is: " + userId);
        } else {
            throw new IllegalArgumentException("No matching user found");
        }
    }
    public void resetPasswordByUserEmail(String userEmail) {
        Optional<User> user = userRepository.findByUserEmail(userEmail);

        if (user.isPresent()) {

            String newPassword = generateRandomPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.get().setUserPw(encodedPassword);
            userRepository.save(user.get());

            emailService.sendEmail(userEmail, "Password Reset", "Your new password is: " + newPassword);
        } else {
            throw new IllegalArgumentException("No matching user found");
        }
    }

    private String generateRandomPassword() {
        Random random = new Random();
        int randomInt = 100000 + random.nextInt(900000);
        return String.valueOf(randomInt);
    }

    public void deleteUser(String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userEmail));

        // 탈퇴한 회원 기록을 ExitUser 테이블에 저장
        ExitUser exitUser = new ExitUser();
//        exitUser.setUserId(user.getUserId());
        exitUser.setUserPw(user.getUserPw());
        exitUser.setUserName(user.getUserName());
        exitUser.setUserEmail(user.getUserEmail());
        exitUser.setUserPH(user.getUserPH());
        exitUser.setUserRole(user.getUserRole());
        exitUser.setUserAdr(user.getUserAdr());
        exitUser.setExitDate(LocalDateTime.now());

        exitUserRepository.save(exitUser);

        // User 엔티티 삭제
        userRepository.delete(user);
    }

    public boolean isAdmin(String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userEmail));
        return user.getUserRole() == 0; // Assuming 0 is the role for admin
    }
    public List<ExitUser> getAllExitUsers() {
        return exitUserRepository.findAll();
    }

}

