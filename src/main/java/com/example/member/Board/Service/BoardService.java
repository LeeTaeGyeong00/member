package com.example.member.Board.Service;


import com.example.member.Board.Dto.*;
import com.example.member.Board.Entity.Board;
import com.example.member.Board.Entity.Like;
import com.example.member.Board.Repository.BoardRepository;
import com.example.member.Board.Repository.CommentRepository;
import com.example.member.Board.Repository.LikeRepository;
import com.example.member.User.Entity.User;
import com.example.member.User.Jwt.TokenProvider;
import com.example.member.User.Repository.UserRepository;
import com.example.member.User.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;


    public BoardRequestDto addBoard(HttpServletRequest request, BoardRequestDto boardRequestDto) {
        String token = tokenProvider.resolveToken(request);
        System.out.println("토큰" + token);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);
        System.out.println("이매일" + currentUserEmail);

        User user = userRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 아닙니다." + currentUserEmail));
        System.out.println("회원 검증완료");
        Board board = new Board();
        board.setUser(user);
        System.out.println("회원" + user);
        board.setTitle(boardRequestDto.getTitle());
        System.out.println("제목" + boardRequestDto.getTitle());
        board.setContent(boardRequestDto.getContent());
        System.out.println("내용" + boardRequestDto.getContent());
        boardRepository.save(board);
        return new BoardRequestDto(board.getTitle(), board.getContent());
    }

    public Page<BoardResponseDto> viewBoardList(HttpServletRequest request, Pageable pageable) {
        Page<Board> boards = boardRepository.findAll(pageable);
        Page<BoardResponseDto> boardResponseDto = boards.map(board -> new BoardResponseDto(board));
        return boardResponseDto;
    }

    public Page<BoardResponseDto> viewBoardSearch(String searchKeyword, HttpServletRequest request, Pageable pageable) {
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);
        Page<Board> boards = boardRepository.findByTitleContaining(searchKeyword, pageable);
        Page<BoardResponseDto> boardResponseDto = boards.map(board -> new BoardResponseDto(board));
        return boardResponseDto;
    }

    @Transactional
    public BoardRequestDto editBoard(HttpServletRequest request, HttpServletResponse response, Long id, BoardRequestDto boardRequestDto) {
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);

        User user = userRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 아닙니다." + currentUserEmail));
        System.out.println(user.getUserNo());
        Board board = boardRepository.findByBoardNoAndUser(id, user).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자의 게시글을 찾을 수 없습니다.")
        );

        board.setTitle(boardRequestDto.getTitle());
        board.setContent(boardRequestDto.getContent());

        boardRepository.save(board);

        return new BoardRequestDto(board.getTitle(), board.getContent());

    }


    public BoardResponseDto viewBoard(HttpServletRequest request, HttpServletResponse response, Long id) {
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);

        User user = userRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 아닙니다." + currentUserEmail));

        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시물이 없습니다."));
        // 조회수 처리
        if (!isBoardIdInCookies(request, id)) {
            board.setViewCount(board.getViewCount() + 1);
            boardRepository.save(board);  // 변경된 조회수 저장

            // 쿠키에 게시물 ID 추가 (중복 조회 방지)
            addBoardIdInCookies(response, id);
        }
        boolean isAuthor = board.getUser().getUserNo().equals(user.getUserNo());
        boolean isLike = likeRepository.findByBoardAndUser(board, user).isPresent();


        BoardResponseDto dtoResponse = new BoardResponseDto(board);
        dtoResponse.setAuthor(isAuthor);
        dtoResponse.setLike(isLike);
        return dtoResponse;
    }

    public void remove(Long id) {
        boardRepository.deleteById(id);
    }


    public LikeRequest likeBoard(HttpServletRequest request, Long id) {
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);

        User user = userRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 아닙니다." + currentUserEmail));
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않거나 삭제되었습니다. ID: " + id));
        Optional<Like> dupLike = likeRepository.findByBoardAndUser(board, user);
        if (dupLike.isPresent()) {
            throw new RuntimeException("회원이 이미 좋아요를 눌렀습니다.");
        } else {
            board.setLikeCount(board.getLikeCount() + 1);
            boardRepository.save(board);

            Like like = Like.builder()
                    .user(user)
                    .board(board)
                    .build();
            likeRepository.save(like);

            LikeRequest response = new LikeRequest();
            response.setBoard(id);
            response.setLikeCount(board.getLikeCount());
            return response;
        }
    }

    @Transactional
    public void removeLike(HttpServletRequest request, Long id) {
        String token = tokenProvider.resolveToken(request);
        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);

        User user = userRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 아닙니다." + currentUserEmail));
        //Board board = new Board();
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않거나 삭제되었습니다. ID: " + id));
        // 좋아요 정보 조회 및 삭제
        Like like = likeRepository.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("회원은 이 게시물에 좋아요를 하지 않았습니다."));

        board.setLikeCount(board.getLikeCount() - 1);

        likeRepository.delete(like);
    }

//    public List<Comment> viewComment(HttpServletRequest request, Long boardNo){
//        String token = tokenProvider.resolveToken(request);
//        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);
//
//        User user = userRepository.findByUserEmail(currentUserEmail)
//                .orElseThrow(() -> new UsernameNotFoundException("회원이 아닙니다." + currentUserEmail));
//
//        Board board = boardRepository.findById(boardNo)
//                .orElseThrow(() -> new IllegalArgumentException("게시글이 없거나 삭제 되었습니다."));
//
//        // 댓글 리스트 조회
//        List<Comment> comments = commentRepository.findAllByBoard(board);
//
//        // 댓글 리스트 반환
//        return comments;
//    }
//    public CommentRequestDto writeComment(HttpServletRequest request, Long boardNo, Long mentionedUserNo, Long parentCommentNo){
//        String token = tokenProvider.resolveToken(request);
//        String currentUserEmail = tokenProvider.getUserEmailFromJWT(token);
//
//        User user = userRepository.findByUserEmail(currentUserEmail)
//                .orElseThrow(() -> new UsernameNotFoundException("회원이 아닙니다." + currentUserEmail));
//        Board board = new Board(boardNo);
//
//        Comment comment = Comment.builder()
//                .user(user)
//                .board(board)
//                .parentCommentNo(parentCommentNo)
//                .mentionedUserNo(mentionedUserNo)
//                .build();
//        likeRepository.save(like);
//
//        CommentRequestDto response = new CommentRequestDto();
//    }
//
//    @Transactional
//    public CommentRequestDto editComment(HttpServletRequest request, Long boardId, Long userId){
//
//    }

    private boolean isBoardIdInCookies(HttpServletRequest request, Long boardId) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("viewed_board_" + boardId)) {
                    return true;
                }
            }
        }
        return false;
    }
    private void addBoardIdInCookies(HttpServletResponse response, Long boardId) {
        Cookie cookie = new Cookie("viewed_board_" + boardId, "true");
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}




//
//


