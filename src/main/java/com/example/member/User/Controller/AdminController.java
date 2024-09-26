package com.example.member.User.Controller;

import com.example.member.User.Dto.UserUpdateDTO;
import com.example.member.User.Entity.ExitUser;
import com.example.member.User.Entity.User;
import com.example.member.User.Jwt.TokenProvider;
import com.example.member.User.Service.AdminService;
import com.example.member.User.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final AdminService adminService;
    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private boolean isAdmin(String userId) {
        return userService.isAdmin(userId);
    }

    @GetMapping("/withdraw-users")
    @Operation(
            summary = "탈퇴한 회원 조회",
            description = "탈퇴한 회원을 전체 조회합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴한 회원 조회"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<?> getExitUsers(HttpServletRequest request) {
        // JWT 토큰에서 현재 사용자 ID를 추출
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);

        // 현재 사용자가 관리자 권한을 가지고 있는지 확인
        if (!userService.isAdmin(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to view this resource.");
        }

        // 탈퇴한 사용자 리스트를 반환
        List<ExitUser> exitUsers = userService.getAllExitUsers(); // Use UserService to fetch exit users
        return ResponseEntity.ok(exitUsers);
    }

    @PutMapping("/update-user")
    @Operation(
            summary = "관리자 회원 정보 수정",
            description = "회원 정보 수정",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 수정 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<String> adminUpdateUser(@RequestBody UserUpdateDTO updateDTO, @RequestParam String targetUserId, HttpServletRequest request) {
        // JWT 토큰에서 현재 관리자 ID를 추출
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);

        // 현재 사용자가 관리자인지 확인
        if (!isAdmin(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to perform this action.");
        }

        // 관리자가 대상 사용자 정보를 수정
        adminService.adminUpdateUser(targetUserId, updateDTO);
        return ResponseEntity.ok("User information has been updated.");
    }

    @DeleteMapping("/delete-user")
    @Operation(
            summary = "관리자 회원 강제 탈퇴",
            description = "관리자 회원 강제 탈퇴",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<String> adminDeleteUser(@RequestParam String targetUserId, HttpServletRequest request) {
        // JWT 토큰에서 현재 관리자 ID를 추출
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);

        // 현재 사용자가 관리자인지 확인
        if (!userService.isAdmin(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to perform this action.");
        }

        // 관리자가 대상 사용자를 강제 탈퇴
        try {
            userService.deleteUser(targetUserId);
            return ResponseEntity.ok("User has been forcefully deactivated.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deactivating the user.");
        }
    }

    // 회원 전체 목록 조회
    @GetMapping("/users")
    @Operation(
            summary = "회원 목록 조회",
            description = "회원 목록 조회",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 조회 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        // JWT 토큰에서 현재 사용자 ID를 추출
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);

        // 현재 사용자가 관리자 권한을 가지고 있는지 확인
        if (!userService.isAdmin(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to view this resource.");
        }

        // 모든 사용자 리스트 반환
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search-user")
    @Operation(
            summary = "회원 검색",
            description = "회원 검색",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 검색 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<?> searchUsers(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String userName,
            HttpServletRequest request) {

        // JWT 토큰에서 현재 사용자 ID를 추출
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);

        // 현재 사용자가 관리자 권한을 가지고 있는지 확인
        if (!userService.isAdmin(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to view this resource.");
        }

        // 사용자를 검색하고 결과 반환
        List<User> users = adminService.searchUsers(userId, userName);
        return ResponseEntity.ok(users);
    }
}
