package com.example.member.User.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
//    public void sendResetPasswordEmail(String userEmail) {
//        // 6자리 랜덤 숫자 생성
//        String newPassword = generateRandomPassword();
//
//        // 비밀번호 초기화 로직
//        userService.resetPassword(userEmail, newPassword);
//
//        // 이메일 전송
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(userEmail);
//        message.setSubject("비밀번호 초기화 안내");
//        message.setText("비밀번호가 다음과 같이 초기화되었습니다: " + newPassword);
//
//        mailSender.send(message);
//    }
//
//    private String generateRandomPassword() {
//        Random random = new Random();
//        int randomNumber = 100000 + random.nextInt(900000);
//        return String.valueOf(randomNumber);
//    }
}
