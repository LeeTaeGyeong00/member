package com.example.member.Board.Repository;

import com.example.member.Board.Entity.Board;
import com.example.member.Board.Entity.Comment;
import com.example.member.Board.Entity.Like;
import com.example.member.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByBoardAndUser(Board board, User user);
    List<Like> findByUser(User user);
}
