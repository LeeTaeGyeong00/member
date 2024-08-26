package com.example.member.User.Repository;

import com.example.member.User.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUserId(String userId);
    void deleteByUserId(String userId);
}
