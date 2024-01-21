package hello.dev.controller;

import hello.dev.domain.Board;
import hello.dev.domain.Login;
import hello.dev.domain.Member;
import hello.dev.domain.SessionConst;
import hello.dev.service.BoardService;
import hello.dev.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class LoginController implements LoginControllerInterface {

    private final LoginService loginService;
    private final BoardService boardService;

    // 로그인 페이지
    @Override
    @GetMapping("/login")
    public String loginForm(@ModelAttribute Login login, Board board, Model model, HttpServletRequest request) {
        log.info("<=====LoginController.loginForm=====>");

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        return "loginForm";
    }

    @Override
    @PostMapping("/login")
    public String login(/*@Valid*/ @ModelAttribute Login login, @ModelAttribute Board board
            , BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request
            , RedirectAttributes redirectAttributes
            , Model model) {
        log.info("<=====LoginController.loginForm=====>");

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        redirectAttributes.addFlashAttribute("board", board);

        // 아아디, 비밀번호 null 체크
//        bindingResult = loginService.checkError(login, bindingResult);

//        if (bindingResult.hasErrors()) {
//            log.info("error = {}", bindingResult);
//
//            return "loginForm";
//        }

        // 아아디, 비밀번호 null 체크
        Map<String, String> errors = loginService.checkError(login);

        if (!errors.isEmpty()) {
            log.info("error = {}", errors);

            model.addAttribute("errors", errors);

            return "loginForm";
        }

        // 계정 정보 조회
        Member member = loginService.login(login.getUserId(), login.getPassword());

        // 계정 정보 확인
        errors = loginService.checkIdPassword(member);

        if (!errors.isEmpty()) {
            log.info("error = {}", errors);

            model.addAttribute("errors", errors);

            return "loginForm";
        }

//        Cookie cookie = new Cookie("userId", member.getUserId());
//        response.addCookie(cookie);

        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession();

        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        redirectAttributes.addFlashAttribute("member", member);
        redirectAttributes.addFlashAttribute("login", login);

        return "redirect:/";
    }

    @Override
    @PostMapping("/logout")
    public String logout(HttpServletResponse response, HttpServletRequest request) {
        log.info("<=====LoginController.logout=====>");

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate(); // 세션 제거
        }

        return "redirect:/";
    }
}
