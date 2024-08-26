package com.example.member.User.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenDto {
    private String grantType; // 형식 Bearer
    private String accessToken;
    private String refreshToken;
    private String userName;
}
