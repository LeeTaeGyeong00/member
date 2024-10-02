package com.example.member.Board.Dto;

import com.example.member.Board.Entity.Board;
import com.example.member.User.Dto.UserInfoDto;
import com.example.member.User.Entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardResponseDto {
    private final Long boardNo;
    private final UserInfoDto userInfoDto;
    private final String title;
    private final String content;
    private final Long viewCount;
    private final Long likeCount;
    private final LocalDateTime regDate;
    private final LocalDateTime modDate;
    @JsonProperty("isAuthor")
    private boolean isAuthor;
    @JsonProperty("isLike")
    private boolean isLike;

    public BoardResponseDto(Board board) {
        this.boardNo = board.getBoardNo();
        User user = board.getUser();
        this.userInfoDto = new UserInfoDto(user.getUserNo(), user.getUserName());
        this.title = board.getTitle();
        this.content = board.getContent();
        this.viewCount = board.getViewCount();
        this.likeCount = board.getLikeCount();
        this.regDate = board.getRegDate();
        this.modDate = board.getModDate();
    }
}
