package com.example.member.Board.Entity;

import com.example.member.User.Entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "userLike")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeNo;

    @ManyToOne
    @JoinColumn(name ="userNo")
    private User user;

    @ManyToOne
    @JoinColumn(name="boardNo")
    private Board board;

    @CreationTimestamp
    private LocalDateTime likeDate;



    public Like(User user, Board board) {
    }

}
