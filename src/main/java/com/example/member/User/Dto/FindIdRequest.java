package com.example.member.User.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindIdRequest {
    private String userName;
    private String userEmail;
}