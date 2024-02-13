package hello.dev.repository;

import hello.dev.domain.Board;
import hello.dev.domain.Member;
import hello.dev.mybatis.LoginMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LoginRepository {

    private final LoginMapper loginMapper;
    private static final Map<Integer, Board> board = new HashMap<>();

    public Member login(String userId, String password, String provider) {
        log.info("<=====LoginRepository.login=====>");

        return loginMapper.login(userId, password, provider);
    }
}