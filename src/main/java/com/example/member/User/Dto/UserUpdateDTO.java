package com.example.member.User.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String userId;
    private String userPw;
    private String userName;
    private String userEmail;
    private String userPH;
    private String userAdr;
}