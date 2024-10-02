package com.example.member.Board.Controller;

import com.example.member.Board.Dto.*;
import com.example.member.Board.Service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    //게시판 조회
    @GetMapping("/detail/{id}")
    @Operation(
            summary = "게시물 상세 페이지 조회",
            description = "게시물 상세 페이지 조회 ",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 페이지 조회 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<BoardResponseDto> detail(HttpServletRequest request,HttpServletResponse response, @PathVariable Long id){
        BoardResponseDto boardView = boardService.viewBoard(request, response,id);
        return ResponseEntity.ok(boardView);
    }
    @GetMapping("/list/{id}")
    @Operation(
            summary = "게시물 리스트 페이지 조회",
            description = "게시물 리스트 페이지 조회 ",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 리스트 페이지 조회 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public Page<BoardResponseDto> boards(
            HttpServletRequest request,
            @PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable) {


        return boardService.viewBoardList(request, pageable);
    }
    //게시판 검색
    @GetMapping("/search")
    @Operation(
            summary = "게시물 검색 페이지 조회",
            description = "게시물 검색 페이지 조회 ",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public  ResponseEntity<Page<BoardResponseDto>> boardSearch(
            HttpServletRequest request,@RequestParam(value = "title", required = false)String searchKeyword,
            @PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC)Pageable pageable,
            String title){
        Page<BoardResponseDto> boardSearch = boardService.viewBoardSearch(searchKeyword,request, pageable);
        return ResponseEntity.ok(boardSearch);

    }
    //게시판 등록
    @PostMapping("/write")
    @Operation(
            summary = "게시물 등록",
            description = "게시물 등록 ",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<BoardRequestDto> write(HttpServletRequest request, @RequestBody BoardRequestDto boardRequestDto){
        System.out.println("컨트롤러 시작");
        BoardRequestDto addBoard = boardService.addBoard(request, boardRequestDto);
        System.out.println(addBoard);
        return ResponseEntity.ok(addBoard);
    }
    //게시판 수정 보내기
    @PutMapping("/update/{id}")
    @Operation(
            summary = "게시물 수정",
            description = "게시물 수정 ",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<BoardRequestDto> updateBoard(HttpServletRequest request, HttpServletResponse response,@PathVariable Long id, @RequestBody BoardRequestDto boardRequestDto){
        BoardRequestDto addBoard = boardService.editBoard(request, response, id, boardRequestDto);
        return ResponseEntity.ok(addBoard);
    }
    //게시판 삭제
    @DeleteMapping("/remove/{id}")
    @Operation(
            summary = "게시물 삭제",
            description = "게시물 삭제 ",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 완료"),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<String> removeBoard(@PathVariable Long id){
        boardService.remove(id);
        return ResponseEntity.status(HttpStatus.OK).body("정상 삭제 완료");
    }

    //좋아요 등록
    @PostMapping("/like/{id}")
    @Operation(
            summary = "게시물 좋아요",
            description = "게시물 좋아요 ",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 완료"),
            @ApiResponse(responseCode = "500", description = "회원이 이미 좋아요를 눌렀습니다."),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<LikeRequest> addLike(HttpServletRequest request, @PathVariable Long id){
        LikeRequest addLike=boardService.likeBoard(request, id);
        return ResponseEntity.ok(addLike);
    }

    //좋아요 삭제
    @DeleteMapping("/like/{id}")
    @Operation(
            summary = "게시물 좋아요",
            description = "게시물 좋아요 ",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 완료"),
            @ApiResponse(responseCode = "500", description = "회원이 이미 좋아요 취소 했습니다."),
            @ApiResponse(responseCode = "403", description = "액세스 토큰이 없습니다.")
    })
    public ResponseEntity<?> removeLike(HttpServletRequest request, @PathVariable Long id){
        boardService.removeLike(request, id);
        return ResponseEntity.ok("삭제완료");
    }
    //댓글 조회
//    @GetMapping("/comments/{boardId}")
//    public ResponseEntity<List<Comment>> viewComment(HttpServletRequest request, @PathVariable Long boardId){
//        List<Comment> viewComments=boardService.viewComment(request, boardId);
//        return ResponseEntity.ok(viewComments);
//    }
//    //댓글 작성
//    @PostMapping("/comment/{boardId}")
//    public ResponseEntity<CommentRequestDto> writeComment(HttpServletRequest request, @PathVariable Long boardNo, @RequestBody(required = false) Long mentionedUserNo, @RequestBody(required = false) Long parentCommentNo){
//        CommentRequestDto writeComment=boardService.writeComment(request, boardNo, mentionedUserNo, parentCommentNo);
//        return ResponseEntity.ok(writeComment);
//    }
    //언급 댓글 작성

    //댓글 수정
//    @PutMapping
//    public ResponseEntity<CommentRequestDto> editComment(HttpServletRequest request, @PathVariable Long boardId, @PathVariable(required = false) Long boardId){
//        CommentRequestDto writeComment=boardService.editComment(request, boardId, userId);
//        return ResponseEntity.ok(writeComment);
//    }
    //언급 댓글 삭제
    //@DeleteMapping
    //관리자 게시물 강제 삭제

}
