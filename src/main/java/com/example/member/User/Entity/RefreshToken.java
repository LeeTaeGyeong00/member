package com.example.member.User.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String userId;

    @Column(nullable = false, length = 255)
    private String token;

    public RefreshToken(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}