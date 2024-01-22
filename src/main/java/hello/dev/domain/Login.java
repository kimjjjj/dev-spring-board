package hello.dev.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class Login {

    private String userId; // id

    private String password; // 비밀번호
    private String errorTxt;
}
