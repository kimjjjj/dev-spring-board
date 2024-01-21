package hello.dev.repository;

import hello.dev.domain.Member;

public interface LoginRepositoryInterface {

    Member login(String userId, String password);
}
