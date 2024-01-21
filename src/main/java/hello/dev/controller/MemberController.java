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
public class MemberController implements MemberControllerInterface {

    private final MemberService memberService;
    private final BoardService boardService;
    private final ImageUploadController imageUploadController;

    // 회원가입 페이지
    @Override
    @GetMapping("/join")
    public String joinForm(Board board, Model model, HttpServletRequest request) {
        log.info("<=====MemberController.joinForm=====>");

        model.addAttribute("member", new Member());

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        return "JoinForm";
    }

    // id 중복 체크
//    @Override
//    @ResponseBody
//    @GetMapping("/chkId/{userId}")
//    public Integer chkId(@PathVariable String userId) {
//        log.info("<=====MemberController.chkId=====>");
//        Integer cnt = memberService.findByIdOrNick(userId, "id");
//
//        return cnt;
//    }

    // 회원가입
    @Override
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

        model.addAttribute("member", memberService.save(member));

        // alert창 띄우기
        model.addAttribute("message", "가입되었습니다.");
        model.addAttribute("searchUrl", "/login");

        return "message";
    }

    // 게시판 즐겨찾기
    @Override
    @PostMapping("/favorite/{titleCode}")
    @ResponseBody
//    public String favorite(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, @PathVariable String titleCode, Model model) {
    public Map<String, String> favorite(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                                           @PathVariable String titleCode, @RequestParam String type, Model model, HttpServletRequest request) {
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
    @Override
    @GetMapping("/mypage")
    public String mypage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, @ModelAttribute Board board, Model model, HttpServletRequest request) {
        log.info("<=====MemberController.mypage=====>{}", member.getProfileName());

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);

        model.addAttribute("member", member);

        return "mypageForm";
    }

    // 마이페이지 정보수정
    @Override
    @PostMapping("/mypage")
    public String saveMypage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @RequestParam String nickName
            , @ModelAttribute Board board, Model model, HttpServletRequest request) throws ServletException, IOException {
        log.info("<=====MemberController.saveMypage=====>{}", member.getProfileName());

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);

        // 닉네임 중복 체크
        Map<String, String> errors = memberService.checkNick(nickName);

        model.addAttribute("board", board);

        // request에서 파일 이름 가져오기
        String fileName = imageUploadController.getFileName(null, request);

        // 프로필 이미지 이름&경로 세팅
        if ("".equals(member.getProfileName()) || member.getProfileName() == null || (!"".equals(fileName) && member.getProfileName() != fileName)) {
            // 이미지 이름 세팅
            String uploadFileName = imageUploadController.setFileName(fileName);

            // 이미지 경로 세팅
            String imgUploadPath = imageUploadController.setFilePath(uploadFileName);

            member.setProfileName(uploadFileName);
            member.setProfilePath(imgUploadPath);

            memberService.setProfile(imgUploadPath, request);

        } else if (!"".equals(member.getProfileName()) && member.getProfileName() != null && !errors.isEmpty()) {
            log.info("error = {}", errors);

            model.addAttribute("errors", errors);

            return "mypageForm";
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
    @Override
    @PostMapping("/delete")
    public String delete(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, HttpServletRequest request) {
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

    // 사용자 차단
    @Override
    @PostMapping("/board/{titleCode}/{seq}/addBlock")
    public String addBlock(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
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
    @Override
    @PostMapping("/board/{titleCode}/{seq}/deleteBlock")
    public String deleteBlock(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , @RequestParam String blockId, Model model) {
        log.info("<=====MemberController.deleteBlock=====> {}, {}", member.getUserId(), blockId);

        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/login");

            return "message";
        }

        memberService.deleteBlock(member.getUserId(), blockId);

        // alert창 띄우기
        model.addAttribute("message", "차단 해제되었습니다.");
        model.addAttribute("searchUrl", "/board/" + titleCode + "/" + seq);

        return "message";
    }

    // 마이페이지 사용자 차단해제
    @Override
    @PostMapping("/mypageDeleteBlock")
    public String mypageDeleteBlock(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @RequestParam String blockId, Model model) {
        log.info("<=====MemberController.deleteBlock=====>{}", blockId);

        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/login");

            return "message";
        }

        memberService.deleteBlock(member.getUserId(), blockId);

        // alert창 띄우기
        model.addAttribute("message", "차단 해제되었습니다.");
        model.addAttribute("searchUrl", "/mypage/block");

        return "message";
    }
}
