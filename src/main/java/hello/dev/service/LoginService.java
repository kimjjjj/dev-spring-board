package hello.dev.service;

import hello.dev.domain.*;
import hello.dev.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository loginRepository;
    private final BoardService boardService;

    public Member login(String userId, String password, String provider) {
        log.info("<=====LoginService.login=====>");

        // 계정 정보
        Member login = loginRepository.login(userId, password, provider);

        if (login == null) {
            login = new Member();
        }

        // 즐겨찾기 코드->이름 변환
        Map<String, String> favoriteMap = boardService.boardCodeSet(false);

        login.setFavoriteName1(favoriteMap.get(login.getFavorite1()));
        login.setFavoriteName2(favoriteMap.get(login.getFavorite2()));
        login.setFavoriteName3(favoriteMap.get(login.getFavorite3()));
        login.setFavoriteName4(favoriteMap.get(login.getFavorite4()));
        login.setFavoriteName5(favoriteMap.get(login.getFavorite5()));
        login.setFavoriteName6(favoriteMap.get(login.getFavorite6()));
        login.setFavoriteName7(favoriteMap.get(login.getFavorite7()));
        login.setFavoriteName8(favoriteMap.get(login.getFavorite8()));
        login.setFavoriteName9(favoriteMap.get(login.getFavorite9()));
        login.setFavoriteName10(favoriteMap.get(login.getFavorite10()));

        return login;
    }

    public Map<String, String> checkError(Login login) {
        log.info("<=====LoginService.checkError=====>");

        Map<String, String> errors = new HashMap<>();

        if ("".equals(login.getUserId()) || login.getUserId() == "" || login.getUserId() == null) {
            errors.put("userId", "아이디를 입력해 주세요.");
        } else if ("".equals(login.getPassword()) || login.getPassword() == "" || login.getPassword() == null) {
            errors.put("password", "비밀번호를 입력해 주세요.");
        }

        return errors;
    }

    public Map<String, String> checkIdPassword(Member member) {
        log.info("<=====LoginService.checkIdPassword=====>");

        Map<String, String> errors = new HashMap<>();

        if ("".equals(member.getUserId()) || member.getUserId() == "" || member.getUserId() == null) {
            errors.put("userId", "아이디 또는 비밀번호를 잘못 입력했습니다.");
        }

        return errors;
    }

    // 네이버 로그인 api
    public String requestToServer(String apiURL, String headerStr) throws IOException {
        log.info("<=====LoginService.requestToServer=====>");

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");

        if(headerStr != null && !headerStr.equals("")) {
            con.setRequestProperty("Authorization", headerStr);
        }

        int responseCode = con.getResponseCode();
        BufferedReader br;

        if(responseCode == 200) { // 정상 호출
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {  // 에러 발생
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        String inputLine;
        StringBuffer res = new StringBuffer();
        while ((inputLine = br.readLine()) != null) {
            res.append(inputLine);
        }

        br.close();

        if(responseCode==200) {
            return res.toString();
        } else {
            return null;
        }
    }
}
