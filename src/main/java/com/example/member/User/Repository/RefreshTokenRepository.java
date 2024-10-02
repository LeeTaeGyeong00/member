package com.example.member.User.Repository;

import com.example.member.User.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUserEmail(String userEmail);
    void deleteByUserEmail(String userEmail);
}
