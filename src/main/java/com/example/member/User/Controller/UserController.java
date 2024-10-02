package com.example.member.User.Controller;


import com.example.member.Board.Entity.Board;
import com.example.member.Board.Repository.BoardRepository;
import com.example.member.Board.Service.BoardService;
import com.example.member.User.Dto.FindIdRequest;
import com.example.member.User.Dto.LoginRequest;
import com.example.member.User.Dto.ResetPasswordRequest;
import com.example.member.User.Dto.UserUpdateDTO;
import com.example.member.User.Entity.User;
import com.example.member.User.Jwt.TokenProvider;
import com.example.member.User.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    //private final EmailService emailService;


    @PostMapping("/register")
    @Operation(
            summary = "회원가입",
            description = "회원 가입"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 가입 완료"),
            @ApiResponse(responseCode = "500", description = "조건에 안맞는 내용")
    })
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }


    // 로그아웃 API
    @PostMapping("/userlogout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 가입 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Retrieve refresh token from cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    String userId = tokenProvider.getUserEmailFromJWT(refreshToken);

                    // Remove refresh token from DB
                    userService.removeRefreshToken(userId);

                    // Invalidate the refresh token cookie
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    return ResponseEntity.ok("Logged out successfully");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
    }

    // 로그인 API
    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "로그인"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 가입 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        try {
            System.out.println("이메일"+loginRequest.getUserEmail() +"비번"+ loginRequest.getUserPw());
            // Authenticate user and generate access token
            String accessToken = userService.login(loginRequest.getUserEmail(), loginRequest.getUserPw());
            System.out.println("액세스 토큰 : "+accessToken);
            // Generate refresh token and save to DB
            String refreshToken = userService.generateRefreshToken(loginRequest.getUserEmail());
            System.out.println("리프레쉬 토큰 : "+refreshToken);
            userService.saveRefreshToken(loginRequest.getUserEmail(), refreshToken);

            // Set refresh token as HTTP-only cookie
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 1 week
            response.addCookie(refreshTokenCookie);

            // Return tokens
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials123");
        }
    }
    // 액세스 토큰 갱신 API
    @PostMapping("/refresh")
    @Operation(
            summary = "리프레쉬 토큰 재발급",
            description = "리프레쉬 토큰 재발급",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null) {
            String newAccessToken = userService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(newAccessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
        }
    }
    @GetMapping("/mypage")
    @Operation(
            summary = "마이페이지 조회",
            description = "마이페이지 조회",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마이페이지 조회 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<?> getMyPage(HttpServletRequest request) {

        // 1. 헤더에서 토큰 추출
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or invalid");
        }

        String token = header.substring(7); // "Bearer " 이후의 토큰 부분 추출

        // 2. 토큰 검증
        if (!tokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // 3. 토큰에서 사용자 ID 추출
        String userEmail = tokenProvider.getUserEmailFromJWT(token);
        System.out.println("유저 ID : "+userEmail);
        // 4. 사용자 정보 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        // 5. 사용자 정보에 따라 비즈니스 로직 실행 (예: 마이페이지 정보 조회)
        User user = userService.getUserDetails(userDetails.getUsername());

        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    @Operation(
            summary = "회원 수정",
            description = "회원 수정",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 수정 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<?> updateUserInfo(@RequestBody UserUpdateDTO updateDTO, HttpServletRequest request) {
        // 서비스 메서드를 통해 사용자 정보 업데이트 및 새 토큰 발급
        String newAccessToken = userService.updateUser(updateDTO, request);

        // 응답에 새로 발급된 토큰 포함
        Map<String, String> response = new HashMap<>();
        response.put("newAccessToken", newAccessToken);

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/find-id")
//    @Operation(
//            summary = "ID 찾기",
//            description = "ID 찾기"
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "ID 이메일 발송 완료"),
//            @ApiResponse(responseCode = "500", description = "잘못된 이름 혹은 이메일")
//    })
//    public ResponseEntity<String> findId(@RequestBody FindIdRequest findIdRequest) {
//        userService.findIdByNameAndEmail(findIdRequest.getUserName(), findIdRequest.getUserEmail());
//        return ResponseEntity.ok("Your User ID has been sent to your email.");
//    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "비밀번호 초기화",
            description = "비밀번호 초기화"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초기화 이메일 발송 완료"),
            @ApiResponse(responseCode = "500", description = "잘못된 ID 혹은 Pw")
    })
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        userService.resetPasswordByUserEmail(resetPasswordRequest.getUserEmail());
        return ResponseEntity.ok("Your new password has been sent to your email.");
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "회원탈퇴",
            description = "회원 탈퇴",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초기화 이메일 발송 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        // JWT 토큰에서 userNo 추출
        String token = tokenProvider.resolveToken(request);
        String userId = tokenProvider.getUserEmailFromJWT(token);

        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User account has been deactivated.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    //등록한 게시글 확인
    @GetMapping("/boards")
    @Operation(
            summary = "등록한 게시판 조회",
            description = "등록한 게시판 조회",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시판 조회 성공"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<List<Board>> userBoards(HttpServletRequest request) {
        List<Board> boards = userService.userBoards(request);
        return ResponseEntity.ok(boards);
    }

    @GetMapping("/board-like")
    @Operation(
            summary = "좋아요 누른 게시판 조회",
            description = "좋아요 누른 게시판 조회",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 게시판 조회 성공"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<List<Board>> userLike(HttpServletRequest request) {
        List<Board> likedBoards = userService.userLike(request);
        return ResponseEntity.ok(likedBoards);
    }

}
