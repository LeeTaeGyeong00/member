CREATE TABLE USER(
                     userNo bigint(10) auto_increment,
                     userId VARCHAR(50) NOT NULL,
                     userPw VARCHAR(50) NOT NULL,
                     userEmail VARCHAR(80) NOT NULL,
                     userPH VARCHAR(15) NOT NULL,
                     userRole BIGINT NOT NULL,
                     userAdr VARCHAR(100) NOT NULL,
                     regDate DATE NOT NULL,
                     modDate timestamp NOT NULL,
                     exitDate timestamp,
                     primary key(userNo)
);

CREATE TABLE EXITUSER(
                         exitNo bigint(10) auto_increment,
                         userNo bigint(10) Not Null,
                         primary key(exitNo),
                         foreign key(userNo) references USER(userNo)
);

CREATE TABLE Board (
                       boardNo BIGINT(10) AUTO_INCREMENT, //게시판 고유 넘버
                       userNo BIGINT(10) NOT NULL, //회원 고유 넘버
                       title VARCHAR(255) NOT NULL, // 제목
                       content TEXT NOT NULL, //내용
                       viewCount BIGINT(10) DEFAULT 0, //조회수
                       likeCount BIGINT(10) DEFAULT 0, //좋아요 수
                       regDate DATE NOT NULL, //생성일자
                       modDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, //수정 일자
                       PRIMARY KEY (boardNo),
                       FOREIGN KEY (userNo) REFERENCES USER(userNo)
);

CREATE TABLE Comment (
                         commentNo BIGINT(10) AUTO_INCREMENT, //댓글 고유 번호
                         boardNo BIGINT(10) NOT NULL, //게시판 고유 번호
                         parentCommentNo BIGINT(10) DEFAULT NULL, //답글 고유 번호
                         mentionedUserNo BIGINT(10) DEFAULT NULL, //답글의 작성자 고유 번호
                         content TEXT NOT NULL, //댓글 내용
                         userNo BIGINT(10) NOT NULL, // 작성자 고유번호
                         regDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, //생성일자
                         modDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, //수정일자
                         PRIMARY KEY (commentNo),
                         FOREIGN KEY (boardNo) REFERENCES Board(boardNo),
                         FOREIGN KEY (parentCommentNo) REFERENCES Comment(commentNo),
                         FOREIGN KEY (mentionedUserNo) REFERENCES USER(userNo),
                         FOREIGN KEY (userNo) REFERENCES USER(userNo)
);

CREATE TABLE UserLike (
                          likeNo BIGINT(10) AUTO_INCREMENT, //좋아요 고유번호
                          userNo BIGINT(10) NOT NULL, // 회원 고유번호
                          boardNo BIGINT(10) NOT NULL, // 게시판 고유번호
                          likeDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, //좋아요등록 일자
                          PRIMARY KEY (userNo, boardNo),
                          FOREIGN KEY (userNo) REFERENCES USER(userNo),
                          FOREIGN KEY (boardNo) REFERENCES Board(boardNo)
);
/*
CREATE TABLE SOCIAL_USER(
	socialNo bigint(10) auto_increment,
	userNo bigint(10) NOT NULL,
    type varchar(50) NOT NULL,
    ACCESS_TOKEN varchar(50) NOT NULL,
    primary key(userNo),
    foreign key(userNo) references USER(userNo)
);
*/