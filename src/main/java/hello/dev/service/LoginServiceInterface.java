package hello.dev.service;

import hello.dev.domain.Login;
import hello.dev.domain.Member;

import java.util.Map;

public interface LoginServiceInterface {

    Member login(String userId, String password);

    Map<String, String> checkError(Login login);

    Map<String, String> checkIdPassword(Member member);
}
