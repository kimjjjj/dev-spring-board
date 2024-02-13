package hello.dev.controller;

import hello.dev.domain.Block;
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
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BoardService boardService;
    private final ImageUploadController imageUploadController;

    // 회원가입 페이지
    @GetMapping("/join")
    public String joinForm(Board board, Model model, HttpServletRequest request) {
        log.info("<=====MemberController.joinForm=====>");

        model.addAttribute("member", new Member());

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        return "JoinForm";
    }

    // 회원가입
    @PostMapping("/join")
    public String save(/*@ModelAttribute("board")*/ Member member, @ModelAttribute Board board, Model model, HttpServletRequest request) {
        log.info("<=====MemberController.save=====>");

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        Map<String, String> errors = memberService.checkError(member);

        if (!errors.isEmpty()) {
            log.info("error = {}", errors);

            model.addAttribute("errors", errors);

            return "JoinForm";
        }

        // 휴대폰번호 '-' 세팅
        member.setPhoneNumber(memberService.phoneSetting(member.getPhoneNumber()));

        // 회원가입 - 자체
        member.setProvider("web");

        model.addAttribute("member", memberService.save(member));

        // alert창 띄우기
        model.addAttribute("message", "가입되었습니다.");
        model.addAttribute("searchUrl", "/login");

        return "message";
    }

    // 게시판 즐겨찾기
    @PostMapping("/favorite/{titleCode}")
    @ResponseBody
//    public String favorite(@hello.dev.argumentresolver.Login Member member, @PathVariable String titleCode, Model model) {
    public Map<String, String> favorite(@hello.dev.argumentresolver.Login Member member,
                                           @PathVariable String titleCode, @RequestParam String type, Model model, HttpServletRequest request) {
        log.info("<=====MemberController.favorite=====>");

        Map<String, String> map = new HashMap<>();

        // 계정이 있다면
        if (member == null) {
            map.put("RESULT", "FAIL");
        } else {
            // 계정 포인트 조회
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));

            if ("add".equals(type)) {
                member = memberService.addFavorite(member.getUserId(), titleCode, member);
                map.put("RESULT", "ADD");

            } else {
                member = memberService.removeFavorite(member.getUserId(), titleCode, member);
                map.put("RESULT", "REMOVE");
            }
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_MEMBER, member);

            model.addAttribute("member", member);
        }

        return map;
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String mypage(@hello.dev.argumentresolver.Login Member member, @ModelAttribute Board board, Model model, HttpServletRequest request) {
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
    public String saveMypage(@hello.dev.argumentresolver.Login Member member
            , @RequestParam String nickName
            , @ModelAttribute Board board, Model model, HttpServletRequest request) throws ServletException, IOException {
        log.info("<=====MemberController.saveMypage=====>");

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);

        // 닉네임 에러 체크
        Map<String, String> errors = memberService.checkNick(nickName);

        model.addAttribute("board", board);

        // request에서 파일 이름 가져오기
        String fileName = imageUploadController.getFileName(null, request);

        if (!errors.isEmpty()) {
            log.info("error = {}", errors);

            model.addAttribute("errors", errors);
            model.addAttribute("member", member);

            return "mypageForm";
        }
        // 프로필 이미지 이름&경로 세팅
        else if ("".equals(member.getProfileName()) || member.getProfileName() == null || (!"".equals(fileName) && member.getProfileName() != fileName)) {
            // 이미지 이름 세팅
            String uploadFileName = imageUploadController.setFileName(fileName);

            // 이미지 경로 세팅
            String imgUploadPath = imageUploadController.setFilePath(uploadFileName);

            member.setProfileName(uploadFileName);
            member.setProfilePath(imgUploadPath);

            memberService.setProfile(imgUploadPath, request);

        }

        // 회원정보 update
        member = memberService.saveMypage(member, member.getUserId(), nickName, member.getProfileName(), member.getProfilePath());

        model.addAttribute("member", member);

        // alert창 띄우기
        model.addAttribute("message", "변경되었습니다.");
        model.addAttribute("searchUrl", "/mypage");

        return "message";
    }

    // 회원탈퇴
    @PostMapping("/delete")
    public String delete(@hello.dev.argumentresolver.Login Member member
            , HttpServletRequest request, Model model, HttpSession session) throws IOException {
        log.info("<=====MemberController.delete=====>");

        // 데이터 삭제
        memberService.delete(member.getUserId(), session);

        // 세션 제거
        session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        // alert창 띄우기
        model.addAttribute("message", "탈퇴되었습니다.");
        model.addAttribute("searchUrl", "/");

        return "message";
    }

    // 사용자 차단
    @PostMapping("/board/{titleCode}/{seq}/addBlock")
    public String addBlock(@hello.dev.argumentresolver.Login Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , @ModelAttribute Block block, @RequestParam String boardId, Model model) {
        log.info("<=====MemberController.addBlock=====>{}, {}", member.getUserId(), boardId);

        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/login");

            return "message";
        }

        if (block == null) {
            block = new Block();
        }

        memberService.addBlock(block, member.getUserId(), boardId);

        // alert창 띄우기
        model.addAttribute("message", "차단 되었습니다.");
        model.addAttribute("searchUrl", "/board/" + titleCode + "/" + seq);

        return "message";
    }

    // 사용자 차단해제
    @PostMapping("/board/{titleCode}/{seq}/cancelBlock")
    public String cancelBlock(@hello.dev.argumentresolver.Login Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , @RequestParam String blockId, Model model) {
        log.info("<=====MemberController.cancelBlock=====> {}, {}", member.getUserId(), blockId);

        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/login");

            return "message";
        }

        memberService.cancelBlock(member.getUserId(), blockId);

        // alert창 띄우기
        model.addAttribute("message", "차단 해제되었습니다.");
        model.addAttribute("searchUrl", "/board/" + titleCode + "/" + seq);

        return "message";
    }

    // 마이페이지 사용자 차단해제
    @PostMapping("/mypageCancelBlock")
    public String mypageCancelBlock(@hello.dev.argumentresolver.Login Member member
            , @RequestParam String blockId, Model model) {
        log.info("<=====MemberController.mypageCancelBlock=====>{}", blockId);

        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/login");

            return "message";
        }

        memberService.cancelBlock(member.getUserId(), blockId);

        // alert창 띄우기
        model.addAttribute("message", "차단 해제되었습니다.");
        model.addAttribute("searchUrl", "/mypage/block");

        return "message";
    }
}
