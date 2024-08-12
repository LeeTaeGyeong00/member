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