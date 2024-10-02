package com.example.member.Board.Repository;

import com.example.member.Board.Entity.Board;
import com.example.member.User.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board,Long> {
    Optional<Board> findByBoardNoAndUser(Long id, User user);
    Page<Board> findAll(Pageable pageable);
    Page<Board> findByTitleContaining(String title, Pageable pageable);
    List<Board> findByUser(User user);
}
