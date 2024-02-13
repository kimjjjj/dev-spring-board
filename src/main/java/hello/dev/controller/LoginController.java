package hello.dev.controller;

import hello.dev.domain.*;
import hello.dev.service.BoardService;
import hello.dev.service.LoginService;
import hello.dev.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class LoginController {

    // 네이버 로그인 api client_id
    @Value("${api.naver.client_id}")
    private String client_id;

    // 네이버 로그인 api client_secret
    @Value("${api.naver.client_secret}")
    private String client_secret;

    // 네이버 로그인 api url
    @Value("${api.naver.url}")
    private String url;

    // 네이버 로그인 api callback
    @Value("${api.naver.callback}")
    private String callback;

    private final LoginService loginService;
    private final BoardService boardService;
    private final MemberService memberService;

    // 로그인 페이지
    @GetMapping("/login")
    public String loginForm(@ModelAttribute Login login, Board board, Model model, HttpServletRequest request,
                            HttpSession session) throws UnsupportedEncodingException {
        log.info("<=====LoginController.loginForm=====>");

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        /*--- 네이버 로그인 버튼 세팅 ---*/
        String redirectURI = URLEncoder.encode(callback, "UTF-8");

        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString();

        String apiURL = url + "authorize?response_type=code";
        apiURL += String.format("&client_id=%s&redirect_uri=%s&state=%s", "14ID6Fco6XYlqeR6XGPh", redirectURI, state);

        session.setAttribute("state", state);
        model.addAttribute("apiURL", apiURL);
        /*---------------------------*/

        return "loginForm";
    }

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
        Map<String, String> errors = loginService.checkError(login);

        if (!errors.isEmpty()) {
            log.info("error = {}", errors);

            model.addAttribute("errors", errors);

            return "loginForm";
        }

        // 계정 정보 조회
        Member member = loginService.login(login.getUserId(), login.getPassword(), "web");

        // 계정 정보 확인
        errors = loginService.checkIdPassword(member);

        if (!errors.isEmpty()) {
            log.info("error = {}", errors);

            model.addAttribute("errors", errors);

            return "loginForm";
        }

        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession();

        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        redirectAttributes.addFlashAttribute("member", member);
        redirectAttributes.addFlashAttribute("login", login);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response, HttpServletRequest request) {
        log.info("<=====LoginController.logout=====>");

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate(); // 세션 제거
        }

        return "redirect:/";
    }

    // 네이버 로그인 callback
    @GetMapping("/naver/callback")
    public String naverCallback(@ModelAttribute Board board, HttpServletRequest request, Model model
            , HttpSession session, RedirectAttributes redirectAttributes) throws IOException, ParseException {

        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String redirectURI = URLEncoder.encode(callback, "UTF-8");

        String apiURL = url + "token?grant_type=authorization_code&";
        apiURL += "client_id=" + client_id;
        apiURL += "&client_secret=" + client_secret;
        apiURL += "&redirect_uri=" + redirectURI;
        apiURL += "&code=" + code;
        apiURL += "&state=" + state;

        String res = loginService.requestToServer(apiURL, "");

        if(res != null && !res.equals("")) {
            model.addAttribute("res", res);
            Map<String, Object> parsedJson = new JSONParser(res).parseObject();

            session.setAttribute("currentUser", res);
            session.setAttribute("currentAT", parsedJson.get("access_token"));
            session.setAttribute("currentRT", parsedJson.get("refresh_token"));

            String resProfile = loginService.requestToServer("https://openapi.naver.com/v1/nid/me", "Bearer " + parsedJson.get("access_token"));
            Map<String, Object> profileJsonMap = new JSONParser(resProfile).parseObject();
            Map<String, String> profileMap = (Map<String, String>) profileJsonMap.get("response");

            // 개인정보 세팅
            Member member = new Member();
            member.setUserId(profileMap.get("email")); // id

            // ID 체크
            int cntId = memberService.checkId(profileMap.get("email"));

            // ID없으면 insert
            if (cntId == 0) {
                member.setUserName(profileMap.get("name")); // 이름
                member.setNickName(String.valueOf(UUID.randomUUID())); // 닉네임 랜덤 세팅
                member.setProvider("naver"); // 회원가입 - 네이버

                member = memberService.save(member);
            } else {
                // 계정 정보 조회
                member = loginService.login(profileMap.get("email"), "", "naver");
            }

            //세션에 로그인 회원 정보 보관
            session.setAttribute(SessionConst.LOGIN_MEMBER, member);

            redirectAttributes.addFlashAttribute("member", member);
        } else {
            model.addAttribute("res", "Login failed!");
        }

        return "redirect:/";
    }
}
