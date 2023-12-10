package hello.dev.controller;

import hello.dev.domain.*;
import hello.dev.repository.MemberRepository;
import hello.dev.service.BoardService;
import hello.dev.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    // 첫 화면
    @GetMapping("/")
//    @ResponseBody
    public String chimList(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Login login, @ModelAttribute Board board
            , @RequestParam(required = true, defaultValue = "1") Integer page
            , HttpServletRequest request, Model model) throws SQLException {
        log.info("<=====BoardController.chimList=====>");

        model.addAttribute("boards", boardService.chimList(page));

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);

        // 페이징
        board = boardService.setPage(board, page);

        model.addAttribute("board", board);

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
    public String boardList(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, @PathVariable String titleCode
            , @RequestParam(required = true, defaultValue = "1") Integer page
            , Model model, HttpServletRequest request, HttpServletResponse response) throws SQLException {
        log.info("<=====BoardController.boardList=====>");

        // 최근방문 게시판 쿠키에 저장
        boardService.saveCookie(titleCode, request, response);

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, titleCode);

        // 페이징
        board = boardService.setPage(board, page);

        // 계정 포인트 조회
        if (member != null) {
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        } else {
            member = new Member();
        }

        model.addAttribute("member", member);

        // 데이터 조회
        List<Board> boards = new ArrayList<>();
        if (!"chimhaha".equals(titleCode)) {
            boards = boardService.boardList(titleCode, page); // 침하하 제외한 모든 게시판
        } else {
            boards = boardService.chimList(page); // 침하하
        }

        // 게시글 목록
        model.addAttribute("boards", boards);

        // 게시판 이름
//        board board = new board();
        board.setTitleCode(titleCode);

        // 게시판 코드->이름
        Map<String, String> boardMap = boardService.boardCodeSet(true);
        board.setTitle(boardMap.get(titleCode));

        model.addAttribute("board", board);

        return "boardForm";
    }

    // 게시글보기
    @GetMapping("/{seq}")
    public String boardPost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int seq, Model model, HttpServletRequest request) throws SQLException {
        log.info("<=====BoardController.boardPost=====>");

        // 계정 포인트 조회
        if (member != null) {
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        } else {
            member = new Member();
//            member.setUserId("0");
        }

        model.addAttribute("member", member);

        Board board = boardService.boardPost(member.getUserId(), seq);

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        return "postForm";
    }

    // 침하하
    @PostMapping("/{seq}/like")
    public String updateLike(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int seq, Model model) throws SQLException {
        log.info("<=====BoardController.updateLike=====>");

        // 계정 없으면
        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/" + seq);

            return "message";
        } else {
            // 게시글 좋아요 plus
            boardService.updateLike(seq);

            // 유저 포인트 plus
            memberService.updateUserPoint(member.getUserId(), seq);

            // 계정 포인트 조회
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
            model.addAttribute("member", member);

            return "redirect:/{seq}";
        }
    }

    // 침하하 취소
    @PostMapping("/{seq}/cancel")
//    @ResponseBody
    public String cancelLike(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int seq, Model model) throws SQLException {
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

//        HashMap<String, String> resultMap = new HashMap<>();
//
//        // 계정이 있다면
//        if (member.getUserId() != null) {
//            resultMap.put("RESULT","SUCESS");
//        } else {
//            resultMap.put("RESULT","FAIL");
//        }
//        return resultMap;
        }

        return "redirect:/{seq}";
    }

    // 글쓰기 페이지로 이동
    @GetMapping("/add")
    public String addForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , Board board, Model model, HttpServletRequest request) throws SQLException {
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
    public String save(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , /*@ModelAttribute("board")*/ Board board, Model model) throws SQLException {
        log.info("<=====BoardController.save=====>");

        board.setComment(board.getComment().replace("\r\n",""));

        Map<String, String> errors = new HashMap<>();

        if (!StringUtils.hasText(board.getBoardCode())) {
            errors.put("boardCode", "게시판을 선택해 주세요!");
        }

        if (!StringUtils.hasText(board.getTxtName())) {
            errors.put("txtName", "제목을 입력해 주세요!");
        }

        if (!errors.isEmpty()) {
            log.info("error = {}", errors);

            model.addAttribute("errors", errors);

            return "addForm";
        }

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

        return "redirect:/board/all";
    }

    // 검색
    @GetMapping("/search")
    public String search(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, Model model, HttpServletRequest request) throws SQLException {
        log.info("<=====BoardController.search=====>");

        // 계정 포인트 조회
        if (member != null) {
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

        model.addAttribute("boards", boardService.search(board.getSearchKeyword(), board.getSearchType()));

        return "searchForm";
    }

    // 게시판 select박스
    @ModelAttribute("boardCodes")
    public List<BoardCode> boardCodes() {
        log.info("<=====BoardController.boardCodes=====>");
        List<BoardCode> boardCodes = new ArrayList<>();

        Map<String, String> codeMap = boardService.boardCodeSet(true);

        boardCodes.add(new BoardCode("chim", codeMap.get("chim")));
        boardCodes.add(new BoardCode("chim_jjal", codeMap.get("chim_jjal")));
        boardCodes.add(new BoardCode("chim_fanart", codeMap.get("chim_fanart")));
        boardCodes.add(new BoardCode("request_stream", codeMap.get("request_stream")));
        boardCodes.add(new BoardCode("find_chimtube", codeMap.get("find_chimtube")));
        boardCodes.add(new BoardCode("make_short", codeMap.get("make_short")));
        boardCodes.add(new BoardCode("favorite_chimtubu", codeMap.get("favorite_chimtubu")));

//        boardCodes.add(new BoardCode(1, "\uD83D\uDE0A침착맨"));
//        boardCodes.add(new BoardCode(2, "\uD83C\uDF83침착맨 짤"));
//        boardCodes.add(new BoardCode(3, "\uD83C\uDFA8침착맨 팬아트"));
//        boardCodes.add(new BoardCode(4, "\uD83D\uDCE3방송 해줘요"));
//        boardCodes.add(new BoardCode(5, "\uD83C\uDF73침투부 찾아요"));
//        boardCodes.add(new BoardCode(6, "\uD83C\uDFAC쇼츠 만들어줘요"));
//        boardCodes.add(new BoardCode(7, "\uD83D\uDC53재밌게 본 침투부"));

        return boardCodes;
    }

//    @ModelAttribute("categoryCodes")
//    public List<CategoryCode> categoryCodes() {
//        log.info("<=====BoardController.categoryCodes=====>");
//        List<CategoryCode> categoryCodes = new ArrayList<>();
//
//        Map<Integer, String> codeMap = boardService.categoryCodeSet();
//
////        categoryCodes.add(new CategoryCode(1, codeMap.get(1), "chim"));
////        categoryCodes.add(new CategoryCode(2, codeMap.get(2), "chim"));
////        categoryCodes.add(new CategoryCode(3, codeMap.get(3), "chim_jjal"));
////        categoryCodes.add(new CategoryCode(4, codeMap.get(4), "chim_fanart"));
////        categoryCodes.add(new CategoryCode(5, codeMap.get(5), "chim_fanart"));
////        categoryCodes.add(new CategoryCode(6, codeMap.get(6), "chim_fanart"));
////        categoryCodes.add(new CategoryCode(7, codeMap.get(7), "chim_fanart"));
////        categoryCodes.add(new CategoryCode(8, codeMap.get(8), "request_stream"));
////        categoryCodes.add(new CategoryCode(9, codeMap.get(9), "request_stream"));
////        categoryCodes.add(new CategoryCode(10, codeMap.get(10), "request_stream"));
////        categoryCodes.add(new CategoryCode(11, codeMap.get(11), "request_stream"));
////        categoryCodes.add(new CategoryCode(12, codeMap.get(12), "find_chimtube"));
////        categoryCodes.add(new CategoryCode(13, codeMap.get(13), "make_short"));
////        categoryCodes.add(new CategoryCode(14, codeMap.get(14), "favorite_chimtubu"));
//
//        categoryCodes.add(new CategoryCode(1, codeMap.get(1)));
//        categoryCodes.add(new CategoryCode(2, codeMap.get(2)));
//        categoryCodes.add(new CategoryCode(3, codeMap.get(3)));
//        categoryCodes.add(new CategoryCode(4, codeMap.get(4)));
//        categoryCodes.add(new CategoryCode(5, codeMap.get(5)));
//        categoryCodes.add(new CategoryCode(6, codeMap.get(6)));
//        categoryCodes.add(new CategoryCode(7, codeMap.get(7)));
//        categoryCodes.add(new CategoryCode(8, codeMap.get(8)));
//        categoryCodes.add(new CategoryCode(9, codeMap.get(9)));
//        categoryCodes.add(new CategoryCode(10, codeMap.get(10)));
//        categoryCodes.add(new CategoryCode(11, codeMap.get(11)));
//        categoryCodes.add(new CategoryCode(12, codeMap.get(12)));
//        categoryCodes.add(new CategoryCode(13, codeMap.get(13)));
//        categoryCodes.add(new CategoryCode(14, codeMap.get(14)));
//
////        categoryCodes.add(new CategoryCode(1, "침착맨"));
////        categoryCodes.add(new CategoryCode(2, "생중계"));
////        categoryCodes.add(new CategoryCode(3, "침착맨 짤"));
////        categoryCodes.add(new CategoryCode(4, "팬아트"));
////        categoryCodes.add(new CategoryCode(5, "팬무비"));
////        categoryCodes.add(new CategoryCode(6, "팬픽션"));
////        categoryCodes.add(new CategoryCode(7, "팬게임"));
////        categoryCodes.add(new CategoryCode(8, "게임"));
////        categoryCodes.add(new CategoryCode(9, "합방"));
////        categoryCodes.add(new CategoryCode(10, "개인컨텐츠"));
////        categoryCodes.add(new CategoryCode(11, "해줘요"));
////        categoryCodes.add(new CategoryCode(12, "찾아요"));
////        categoryCodes.add(new CategoryCode(13, "쇼츠"));
////        categoryCodes.add(new CategoryCode(14, "추천영상"));
//
//        return categoryCodes;
//    }

    // 카테고리 select박스
    @GetMapping("/categoryCode")
    @ResponseBody
    public List<CategoryCode> categoryC(String boardCode) {
        log.info("<=====BoardController.categoryCode=====>");

        List<CategoryCode> categoryCodes = new ArrayList<>();

        Map<Integer, String> codeMap = boardService.categoryCodeSet();

//        categoryCodes.add(new CategoryCode(1, codeMap.get(1), "chim"));
//        categoryCodes.add(new CategoryCode(2, codeMap.get(2), "chim"));
//        categoryCodes.add(new CategoryCode(3, codeMap.get(3), "chim_jjal"));
//        categoryCodes.add(new CategoryCode(4, codeMap.get(4), "chim_fanart"));
//        categoryCodes.add(new CategoryCode(5, codeMap.get(5), "chim_fanart"));
//        categoryCodes.add(new CategoryCode(6, codeMap.get(6), "chim_fanart"));
//        categoryCodes.add(new CategoryCode(7, codeMap.get(7), "chim_fanart"));
//        categoryCodes.add(new CategoryCode(8, codeMap.get(8), "request_stream"));
//        categoryCodes.add(new CategoryCode(9, codeMap.get(9), "request_stream"));
//        categoryCodes.add(new CategoryCode(10, codeMap.get(10), "request_stream"));
//        categoryCodes.add(new CategoryCode(11, codeMap.get(11), "request_stream"));
//        categoryCodes.add(new CategoryCode(12, codeMap.get(12), "find_chimtube"));
//        categoryCodes.add(new CategoryCode(13, codeMap.get(13), "make_short"));
//        categoryCodes.add(new CategoryCode(14, codeMap.get(14), "favorite_chimtubu"));

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
        }

//        categoryCodes.add(new CategoryCode(1, "침착맨"));
//        categoryCodes.add(new CategoryCode(2, "생중계"));
//        categoryCodes.add(new CategoryCode(3, "침착맨 짤"));
//        categoryCodes.add(new CategoryCode(4, "팬아트"));
//        categoryCodes.add(new CategoryCode(5, "팬무비"));
//        categoryCodes.add(new CategoryCode(6, "팬픽션"));
//        categoryCodes.add(new CategoryCode(7, "팬게임"));
//        categoryCodes.add(new CategoryCode(8, "게임"));
//        categoryCodes.add(new CategoryCode(9, "합방"));
//        categoryCodes.add(new CategoryCode(10, "개인컨텐츠"));
//        categoryCodes.add(new CategoryCode(11, "해줘요"));
//        categoryCodes.add(new CategoryCode(12, "찾아요"));
//        categoryCodes.add(new CategoryCode(13, "쇼츠"));
//        categoryCodes.add(new CategoryCode(14, "추천영상"));

        return categoryCodes;
    }
}
