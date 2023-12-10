package hello.dev.service;

import hello.dev.domain.Login;
import hello.dev.domain.Member;
import hello.dev.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.sql.SQLException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository loginRepository;
    private final BoardService boardService;

    public Member login(String userId, String password) throws SQLException {
        log.info("<=====LoginService.login=====>");

        // 계정 정보
        Member login = loginRepository.login(userId, password);

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

    public BindingResult checkError(Login login, BindingResult bindingResult) {
        if ("".equals(login.getUserId()) || login.getUserId() == "" || login.getUserId() == null) {
            bindingResult.addError(new FieldError("login", "errorTxt", "아이디를 입력해 주세요."));
        } else if ("".equals(login.getPassword()) || login.getPassword() == "" || login.getPassword() == null) {
            bindingResult.addError(new FieldError("login", "errorTxt", "비밀번호를 입력해 주세요."));
        }

        return bindingResult;
    }
}
