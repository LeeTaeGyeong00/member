package com.example.member;

import com.example.member.User.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final AdminService adminService;

    @Override
    public void run(String... args) throws Exception {
        adminService.createAdminAccount();
    }
}