package com.example.member.User.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EXITUSER")
@Getter
@Setter
public class ExitUser {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "exitNo", nullable = false)
//    private Long exitNo;
//
//
//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "userNo", nullable = false)
//    private User user;
//
//    @Column(name = "exitDate", nullable = false)
//    private LocalDateTime exitDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exitNo", nullable = false)
    private Long exitNo;

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

    @Column(name = "exitDate", nullable = false)
    private LocalDateTime exitDate;

}
