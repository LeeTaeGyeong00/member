package com.example.member.User.Service;

import com.example.member.User.Dto.UserUpdateDTO;
import com.example.member.User.Entity.ExitUser;
import com.example.member.User.Entity.User;
import com.example.member.User.Repository.ExitUserRepository;
import com.example.member.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ExitUserRepository exitUserRepository;

    public void createAdminAccount() {
        // 관리자 계정이 이미 존재하는지 확인
        if (!userRepository.existsByUserEmail("admin")) {
            // 관리자 계정 정보 설정
            User admin = new User();
//            admin.setUserId("admin");
            admin.setUserPw(passwordEncoder.encode("admin")); // 비밀번호를 해싱하여 저장
            admin.setUserName("admin");
            admin.setUserEmail("admin@example.com");
            admin.setUserPH("000-0000-0000");
            admin.setUserAdr("Admin Address");
            admin.setUserRole(0L); // 관리자 역할 (userRole = 0)

            // 관리자 계정 저장
            userRepository.save(admin);
            System.out.println("Admin account created.");
        } else {
            System.out.println("Admin account already exists.");
        }
    }

    public void adminUpdateUser(String targetUserId, UserUpdateDTO updateDTO) {
        User user = userRepository.findByUserEmail(targetUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + targetUserId));

        // 사용자 정보 업데이트
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

        user.setModDate(LocalDateTime.now()); // 수정 시간 갱신
        userRepository.save(user);
    }
    public void adminDeleteUser(String targetUserId) {
        // 사용자 정보 조회
        User user = userRepository.findByUserEmail(targetUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + targetUserId));

        // 탈퇴한 회원 기록을 ExitUser 테이블에 저장
        ExitUser exitUser = new ExitUser();
        exitUser.setUserId(user.getUserEmail());
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String userId, String userName) {
        if (userId != null && !userId.isEmpty()) {
            return userRepository.findByUserEmailContaining(userId);
        } else if (userName != null && !userName.isEmpty()) {
            return userRepository.findByUserNameContaining(userName);
        } else {
            return List.of(); // 검색어가 없으면 빈 리스트 반환
        }
    }
}