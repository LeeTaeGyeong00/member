package com.example.member.User.Repository;

import com.example.member.User.Entity.ExitUser;
import com.example.member.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExitUserRepository extends JpaRepository<ExitUser, Long> {
    boolean existsByUserId(String userId);
}
