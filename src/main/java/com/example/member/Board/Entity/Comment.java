package com.example.member.Board.Entity;


import com.example.member.User.Entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Comment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentNo;

    @ManyToOne
    @JoinColumn(name = "boardNo")
    private Board board;
    @ManyToOne
    @JoinColumn(name ="userNo")
    private User user;

//    @Column(name = "parentCommentNo", nullable = true)
    @ManyToOne
    @JoinColumn(name ="parentCommentNo")
    private Comment parentCommentNo;

    @ManyToOne
    @JoinColumn(name ="mentionedUserNo")
    private User mentionedUserNo;

    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "regDate", nullable = false)
    private LocalDateTime regDate;

    @UpdateTimestamp
    @Column(name ="modDate", nullable = false)
    private LocalDateTime modDate;
}
