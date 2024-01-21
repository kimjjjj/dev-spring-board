package hello.dev.service;

import hello.dev.domain.Login;
import hello.dev.domain.Member;
import org.springframework.validation.BindingResult;

import java.util.Map;

public interface LoginServiceInterface {

    Member login(String userId, String password);

//    BindingResult checkError(Login login, BindingResult bindingResult);
    Map<String, String> checkError(Login login);

    Map<String, String> checkIdPassword(Member member);
}
