package com.example.member.Board.Entity;

import com.example.member.User.Entity.User;
import lombok.*;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Board")
@NoArgsConstructor
@Getter
@Setter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardNo", nullable = false)
    private Long boardNo;

    @ManyToOne
    @JoinColumn(name="userNo")
    private User user;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="content", nullable = false)
    private String content;

//    @ColumnDefault("0")
//    @Column(name = "viewCount", nullable = false)
//    private Long viewCount;
//
//    @ColumnDefault("0")
//    @Column(name = "likeCount", nullable = false)
//    private Long likeCount;

    //@ColumnDefault("0") 애너테이션은 엔티티 클래스에서 SQL의 기본값을 지정해주는 역할을 합니다.
    // 하지만 이 애너테이션은 엔티티가 데이터베이스에 직접적으로 저장될 때가 아닌,
    // SQL DDL을 자동 생성할 때만 영향을 미칩니다.
    @ColumnDefault("0")
    @Column(name = "viewCount", nullable = false)
    private Long viewCount = 0L;

    @ColumnDefault("0")
    @Column(name = "likeCount", nullable = false)
    private Long likeCount = 0L;

    @CreationTimestamp
    @Column(name = "regDate", nullable = false)
    private LocalDateTime regDate;

    @UpdateTimestamp
    @Column(name="modDate", nullable = false)
    private LocalDateTime modDate;

    @Builder
    public Board(User user, String title, String content, Long viewCount, Long likeCount) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
    }
}
