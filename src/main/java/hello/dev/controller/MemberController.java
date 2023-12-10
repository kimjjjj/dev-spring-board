package hello.dev.controller;

import hello.dev.domain.Board;
import hello.dev.domain.Member;
import hello.dev.domain.SessionConst;
import hello.dev.service.BoardService;
import hello.dev.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BoardService boardService;

    // 회원가입 페이지
    @GetMapping("/join")
    public String joinForm(Board board, Model model, HttpServletRequest request) {
        log.info("<=====MemberController.joinForm=====>");

        model.addAttribute("member", new Member());

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        return "joinForm";
    }

    // id 중복 체크
//    @ResponseBody
//    @GetMapping("/chkId/{userId}")
//    public Integer chkId(@PathVariable String userId) throws SQLException {
//        log.info("<=====MemberController.chkId=====>");
//        Integer cnt = memberService.findByIdOrNick(userId, "id");
//
//        return cnt;
//    }

    // 회원가입
    @PostMapping("/join")
    public String save(/*@ModelAttribute("board")*/ Member member, @ModelAttribute Board board, Model model, HttpServletRequest request) throws SQLException {
        log.info("<=====MemberController.save=====>");

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        Map<String, String> errors = memberService.checkError(member);

        if (!errors.isEmpty()) {
            log.info("error = {}", errors);

            model.addAttribute("errors", errors);

            return "joinForm";
        }

        model.addAttribute("member", memberService.save(member));

        return "redirect:/login";
    }

    // 게시판 즐겨찾기
    @PostMapping("/favorite/{titleCode}")
    @ResponseBody
//    public String favorite(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, @PathVariable String titleCode, Model model) throws SQLException {
    public Map<String, String> favorite(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                                           @PathVariable String titleCode, @RequestParam String type, Model model, HttpServletRequest request) throws SQLException {
        log.info("<=====MemberController.favorite=====>");

        Map<String, String> map = new HashMap<>();

        // 계정이 있다면
        if (member == null) {
            // alert창 띄우기
//            model.addAttribute("message", "로그인이 필요합니다.");
//            model.addAttribute("searchUrl", "/" + titleCode);
//
//            return "message";
            map.put("RESULT", "FAIL");
        } else {
            // 계정 포인트 조회
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));

            if ("add".equals(type)) {
                member = memberService.addFavorite(member.getUserId(), titleCode, member);
                map.put("RESULT", "ADD");

//                memberService.favoriteList(member, member.getUserId());

//                member = memberService.favoriteList(member, member.getUserId());
//                HttpSession session = request.getSession();
            } else {
                member = memberService.removeFavorite(member.getUserId(), titleCode, member);
                map.put("RESULT", "REMOVE");

//                memberService.favoriteList(member, member.getUserId());

//                member = memberService.favoriteList(member, member.getUserId());
            }
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_MEMBER, member);

            model.addAttribute("member", member);

//            return "redirect:/board/{titleCode}";
        }

        return map;
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String mypage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, @ModelAttribute Board board, Model model, HttpServletRequest request) throws SQLException {
        log.info("<=====MemberController.mypage=====>{}", member.getProfileName());

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);

        model.addAttribute("member", member);

        return "mypageForm";
    }

    // 마이페이지 정보수정
    @PostMapping("/mypage")
    public String saveMypage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @RequestParam String nickName
            , @ModelAttribute Board board, Model model, HttpServletRequest request) throws SQLException, ServletException, IOException {
        log.info("<=====MemberController.saveMypage=====>{}", member.getProfileName());

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);

        // 닉네임 중복 체크
        Map<String, String> errors = memberService.checkNick(nickName);

        model.addAttribute("member", member);
        model.addAttribute("board", board);

        // 닉네임 중복 발생
        if (!errors.isEmpty()) {
            log.info("error = {}", errors);

            model.addAttribute("errors", errors);

            return "mypageForm";
        } else {
            
            // 프로필 이미지 이름 세팅
            member = memberService.setProfile(member, request);

            // 회원정보 update
            member = memberService.saveMypage(member, member.getUserId(), nickName, member.getProfileName(), member.getProfilePath());

            model.addAttribute("member", member);

            // alert창 띄우기
            model.addAttribute("message", "변경되었습니다.");
            model.addAttribute("searchUrl", "/mypage");

            return "message";
        }
    }

    // 회원탈퇴
    @PostMapping("/delete")
    public String delete(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, HttpServletRequest request) throws SQLException {
        log.info("<=====MemberController.delete=====>");

        // 데이터 삭제
        memberService.delete(member.getUserId());

        // 세션 제거
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }
}
