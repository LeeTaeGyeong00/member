package com.example.member.User.Repository;


import com.example.member.User.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserNameAndUserEmail(String userName ,String userEmail);
    boolean existsByUserEmail(String admin);

    List<User> findByUserEmailContaining(String userId);

    List<User> findByUserNameContaining(String userName);
}
