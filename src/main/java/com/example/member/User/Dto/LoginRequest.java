package com.example.member.User.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String userEmail;
    private String userPw;
}
