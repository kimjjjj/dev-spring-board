package hello.dev.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class Login {

//    @NotBlank(message = "아이디를 입력해 주세요.")
    private String userId; // id

//    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password; // 비밀번호
    private String errorTxt;
}
