package hello.dev.controller;

import hello.dev.domain.*;
import hello.dev.service.BoardService;
import hello.dev.service.CommentService;
import hello.dev.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;
    private final CommentService commentService;

    // 첫 화면
    @GetMapping("/")
//    @ResponseBody
    public String chimList(@hello.dev.argumentresolver.Login Member member
            , @ModelAttribute Login login, @ModelAttribute Board board
            , @RequestParam(required = true, defaultValue = "1") Integer page
            , HttpServletRequest request, Model model) {
        log.info("<=====BoardController.chimList=====>");

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);

        // 페이징
        board = boardService.setPage(board, page);

        model.addAttribute("board", board);

        // 로그인 ID
        String userId = null;
        if (member != null) {
            userId = member.getUserId();
        }

        // 게시글 조회
        model.addAttribute("boards", boardService.chimList(page, userId));

        // 세션에 회원 데이터가 없으면 home
        if (member == null) {
            member = new Member();
            model.addAttribute("member", member);
            return "home";
        }

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        model.addAttribute("member", member);

        return "home";
    }

    // 게시판 선택
    @GetMapping("/board/{titleCode}")
    public String boardList(@hello.dev.argumentresolver.Login Member member
            , @ModelAttribute Board board, @PathVariable String titleCode
            , @RequestParam(required = true, defaultValue = "1") Integer page
            , Model model, HttpServletRequest request, HttpServletResponse response) {
        log.info("<=====BoardController.boardList=====>");

        // 최근방문 게시판 쿠키에 저장
        boardService.saveCookie(titleCode, request, response);

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, titleCode);

        // 페이징
        board = boardService.setPage(board, page);

        // 로그인 ID
        String userId = null;

        if (member != null) {
            userId = member.getUserId();

            // 계정 포인트 조회
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        } else {
            member = new Member();
        }

        model.addAttribute("member", member);

        // 데이터 조회
        List<Board> boards = new ArrayList<>();
        if (!"chimhaha".equals(titleCode)) {
            boards = boardService.boardList(titleCode, page, userId); // 침하하 제외한 모든 게시판
        } else {
            boards = boardService.chimList(page, userId); // 침하하
        }

        // 게시글 목록
        model.addAttribute("boards", boards);

        // 게시판 이름
        board.setTitleCode(titleCode);

        // 게시판 코드->이름
        Map<String, String> boardMap = boardService.boardCodeSet(true);
        board.setTitle(boardMap.get(titleCode));

        model.addAttribute("board", board);

        return "boardForm";
    }

    // 게시글보기
    @GetMapping("/board/{titleCode}/{seq}")
    public String boardPost(@hello.dev.argumentresolver.Login Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , Model model, HttpServletRequest request) {
        log.info("<=====BoardController.boardPost=====> titleCode: {}, seq: {}", titleCode, seq);

        // 계정 포인트 조회
        if (member != null) {
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        } else {
            member = new Member();
        }

        model.addAttribute("member", member);
        
        // 게시글 조회
        Board board = boardService.boardPost(member.getUserId(), seq, titleCode);

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        // 댓글 조회
        List<Comment> comments = commentService.findComment(member.getUserId(), seq);
        model.addAttribute("comments", comments);

        return "postForm";
    }

    // 침하하
    @PostMapping("/{seq}/like")
    public String updateLike(@hello.dev.argumentresolver.Login Member member
            , @PathVariable int seq, @RequestParam String titleCode, Model model) {
        log.info("<=====BoardController.updateLike=====> titleCode : {}", titleCode);

        // 계정 없으면
        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
//            model.addAttribute("searchUrl", "/" + titleCode + "/" + seq);
            model.addAttribute("searchUrl", "/login");

            return "message";
        } else {
            // 게시글 좋아요 plus
            boardService.updateLike(seq);

            // 유저 포인트 plus
            memberService.updateUserPoint(member.getUserId(), seq);

            // 계정 포인트 조회
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
            model.addAttribute("member", member);

            return "redirect:/board/" + titleCode + "/{seq}";
        }
    }

    // 침하하 취소
    @PostMapping("/{seq}/cancel")
//    @ResponseBody
    public String cancelLike(@hello.dev.argumentresolver.Login Member member
            , @PathVariable int seq, @RequestParam String titleCode, Model model) {
        log.info("<=====BoardController.cancelLike=====>");

        // 계정이 있다면
        if (member != null) {
            // 게시글 좋아요 minus
            boardService.cancelLike(seq);

            // 유저 포인트 minus
            memberService.cancelUserPoint(member.getUserId(), seq);

            // 계정 포인트 조회
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
            model.addAttribute("member", member);
        }

        return "redirect:/board/" + titleCode + "/{seq}";
    }

    // 스크랩
    @PostMapping("/{seq}/scrapSave")
    public String scrapSave(@hello.dev.argumentresolver.Login Member member
            , @PathVariable int seq, @RequestParam String titleCode, Model model) {
        log.info("<=====BoardController.scrapSave=====> titleCode : {}", titleCode);

        // 계정 없으면
        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/login");

            return "message";
        }

        // 스크랩 저장
        memberService.scrapSave(member.getUserId(), seq);

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        model.addAttribute("member", member);

        return "redirect:/board/" + titleCode + "/{seq}";
    }

    // 스크랩 취소
    @PostMapping("/{seq}/scrapCancel")
    public String scrapCancel(@hello.dev.argumentresolver.Login Member member
            , @PathVariable int seq, @RequestParam String titleCode, Model model) {
        log.info("<=====BoardController.scrapCancel=====>");

        // 계정 없으면
        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/login");

            return "message";
        }

        // 스크랩 취소
        memberService.scrapCancel(member.getUserId(), seq);

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        model.addAttribute("member", member);

        return "redirect:/board/" + titleCode + "/{seq}";
    }

    // 글쓰기 페이지로 이동
    @GetMapping("/add")
    public String addForm(@hello.dev.argumentresolver.Login Member member
            , Board board, Model model, HttpServletRequest request) {
        log.info("<=====BoardController.addForm=====>");

        // 계정 포인트 조회
        if (member != null) {
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        }

        model.addAttribute("member", member);

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        return "addForm";
    }

    // 게시글 저장
    @PostMapping("/add")
    public String save(@hello.dev.argumentresolver.Login Member member
            , /*@ModelAttribute("board")*/ Board board, Model model) {
        log.info("<=====BoardController.save=====>");

        // 계정 포인트 조회
        if (member != null) {
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        }

        model.addAttribute("member", member);

        board.setComment(board.getComment().replace("\r\n",""));

        // 계정
        board.setInsId(member.getUserId());

        model.addAttribute("board", boardService.save(board));

        // 이미지 파일 있으면 이미지 저장 실행
        String[] arr = board.getComment().split("\r\n");

        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (s.contains("<img alt=\"\" src=")) {
                list.add(s);
            }
        }

        if (!list.isEmpty()) {
            boardService.saveImg(board, list);
        }

        // alert창 띄우기
        model.addAttribute("message", "게시글이 등록되었습니다.");
        model.addAttribute("searchUrl", "/board/all");

        return "message";
    }

    // 글수정 페이지로 이동
    @GetMapping("/board/{titleCode}/{seq}/edit")
    public String editForm(@hello.dev.argumentresolver.Login Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , Board board, Model model, HttpServletRequest request) {
        log.info("<=====BoardController.editForm=====>");

        // 계정 포인트 조회
        if (member != null) {
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        }

        model.addAttribute("member", member);

        // 게시글 조회
        board = boardService.editBoardPost(member.getUserId(), seq, titleCode);

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        // 카테고리 세팅
        model.addAttribute("categoryCodes", categoryC(board.getBoardCode()));

        return "editForm";
    }

    // 글수정
    @PostMapping("/board/{titleCode}/{seq}/edit")
    public String updatePost(@hello.dev.argumentresolver.Login Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , Board board, Model model, HttpServletRequest request) {
        log.info("<=====BoardController.updatePost=====>");

        // 계정 포인트 조회
        if (member != null) {
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        }

        model.addAttribute("member", member);

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        // 게시글 update
        boardService.updatePost(board, member.getUserId());

        return "redirect:/board/{titleCode}/{seq}";
    }

    // 게시글 삭제
    @PostMapping("/board/{titleCode}/{seq}/delete")
    public String deletePost(@hello.dev.argumentresolver.Login Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , Board board, Model model, HttpServletRequest request) {
        log.info("<=====BoardController.deletePost=====>");

        // 계정 포인트 조회
        if (member != null) {
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        }

        model.addAttribute("member", member);

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        // 좋아요 delete
        boardService.deleteLike(seq);

        // 댓글 delete
        commentService.deleteCommentBoard(seq);

        // 첨부파일 delete
        boardService.deleteImg(seq);

        // 게시글 delete
        boardService.deletePost(seq);

        return "redirect:/board/{titleCode}";
    }

    // 검색
    @GetMapping("/search")
    public String search(@hello.dev.argumentresolver.Login Member member
            , @ModelAttribute Board board, Model model, HttpServletRequest request) {
        log.info("<=====BoardController.search=====>");

        // 로그인 ID
        String userId = null;

        // 계정 포인트 조회
        if (member != null) {
            userId = member.getUserId();

            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        } else {
            member = new Member();
        }

        model.addAttribute("member", member);

        if (board == null) {
            board = new Board();
        }

        board.setTitle(board.getSearchKeyword()); // 검색페이지 타이틀

        if (board.getSearchType() == null) {
            board.setSearchType("titleAndContent"); // 검색 콤보박스
        }

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        model.addAttribute("boards", boardService.search(board.getSearchKeyword(), board.getSearchType(), userId));

        return "searchForm";
    }

    // 마이페이지 조회
    @GetMapping("/mypage/{mypageTitle}")
    public String mywrite(@hello.dev.argumentresolver.Login Member member
            , @ModelAttribute Board board, @PathVariable String mypageTitle, Model model, HttpServletRequest request) {
        log.info("<=====BoardController.mywrite=====>");

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        model.addAttribute("member", member);

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);

        // 마이페이지 제목
        board.setMypageTitle(boardService.mypageTitle(mypageTitle));

        model.addAttribute("board", board);

        // 조회
        List<Board> boards = boardService.mywrite(member.getUserId(), mypageTitle);
        model.addAttribute("boards", boards);

        return "mywriteForm";
    }

    // 유저 게시글, 댓글 조회
    @GetMapping("/userPage/{nickName}/{userPageTitle}")
    public String userPage(@hello.dev.argumentresolver.Login Member member
            , @ModelAttribute Board board, @PathVariable String nickName, @PathVariable String userPageTitle, Model model) {
        log.info("<=====BoardController.userPage=====>{}, {}", nickName, userPageTitle);

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        model.addAttribute("member", member);

        // 페이지 제목
        board.setUserPageTitle(nickName + boardService.userPageTitle(userPageTitle));
        model.addAttribute("board", board);

        // 조회
        List<Board> boards = boardService.userWrite(nickName, userPageTitle);
        model.addAttribute("boards", boards);

        return "userWriteForm";
    }

    // 게시판 select박스
    @ModelAttribute("boardCodes")
    public List<BoardCode> boardCodes(@hello.dev.argumentresolver.Login Member member) {
        log.info("<=====BoardController.boardCodes=====>");

        if (member == null) {
            member = new Member();
        }

        List<BoardCode> boardCodes = new ArrayList<>();

        Map<String, String> codeMap = boardService.boardCodeSet(true);

        boardCodes.add(new BoardCode("chim", codeMap.get("chim")));
        boardCodes.add(new BoardCode("chim_jjal", codeMap.get("chim_jjal")));
        boardCodes.add(new BoardCode("chim_fanart", codeMap.get("chim_fanart")));
        boardCodes.add(new BoardCode("request_stream", codeMap.get("request_stream")));
        boardCodes.add(new BoardCode("find_chimtube", codeMap.get("find_chimtube")));
        boardCodes.add(new BoardCode("make_short", codeMap.get("make_short")));
        boardCodes.add(new BoardCode("favorite_chimtubu", codeMap.get("favorite_chimtubu")));

        // 계정 타입이 관리자인 경우
        if ("admin".equals(member.getUserType())) {
            boardCodes.add(new BoardCode("notice", codeMap.get("notice")));
        }

        return boardCodes;
    }

    // 카테고리 select박스
    @GetMapping("/categoryCode")
    @ResponseBody
    public List<CategoryCode> categoryC(String boardCode) {
        log.info("<=====BoardController.categoryCode=====>");

        List<CategoryCode> categoryCodes = new ArrayList<>();

        Map<Integer, String> codeMap = boardService.categoryCodeSet();

        if ("chim".equals(boardCode)) {
            categoryCodes.add(new CategoryCode(1, codeMap.get(1)));
            categoryCodes.add(new CategoryCode(2, codeMap.get(2)));
        } else if ("chim_jjal".equals(boardCode)) {
            categoryCodes.add(new CategoryCode(3, codeMap.get(3)));
        } else if ("chim_fanart".equals(boardCode)) {
            categoryCodes.add(new CategoryCode(4, codeMap.get(4)));
            categoryCodes.add(new CategoryCode(5, codeMap.get(5)));
            categoryCodes.add(new CategoryCode(6, codeMap.get(6)));
            categoryCodes.add(new CategoryCode(7, codeMap.get(7)));
        } else if ("request_stream".equals(boardCode)) {
            categoryCodes.add(new CategoryCode(8, codeMap.get(8)));
            categoryCodes.add(new CategoryCode(9, codeMap.get(9)));
            categoryCodes.add(new CategoryCode(10, codeMap.get(10)));
            categoryCodes.add(new CategoryCode(11, codeMap.get(11)));
        } else if ("find_chimtube".equals(boardCode)) {
            categoryCodes.add(new CategoryCode(12, codeMap.get(12)));
        } else if ("make_short".equals(boardCode)) {
            categoryCodes.add(new CategoryCode(13, codeMap.get(13)));
        } else if ("favorite_chimtubu".equals(boardCode)) {
            categoryCodes.add(new CategoryCode(14, codeMap.get(14)));
        } else if ("notice".equals(boardCode)) {
            categoryCodes.add(new CategoryCode(998, codeMap.get(998)));
            categoryCodes.add(new CategoryCode(999, codeMap.get(999)));
        }

        return categoryCodes;
    }
}
