package com.example.member.User.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userNo", nullable = false)
    private Long userNo;

    @Column(name = "userId", nullable = false, length = 50)
    private String userId;

    @Column(name = "userPw", nullable = false, length = 200)
    private String userPw;

    @Column(name = "userName", nullable = false, length = 200)
    private String userName;

    @Column(name = "userEmail", nullable = false, length = 80)
    private String userEmail;

    @Column(name = "userPH", nullable = false, length = 15)
    private String userPH;

    @Column(name = "userRole", nullable = false)
    private Long userRole;

    @Column(name = "userAdr", nullable = false, length = 100)
    private String userAdr;

    @Column(name = "regDate", nullable = false)
    private LocalDate regDate;

    @Column(name = "modDate")
    private LocalDateTime modDate;


    @PrePersist
    protected void onCreate() {
        regDate = LocalDate.now();
    }
}
