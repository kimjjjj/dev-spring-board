package hello.dev.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Member {

    // 계정 정보
    private String userId; // id
    private String password; // 비밀번호
    private String userName; // 이름
    private String birth; // 생년월일
    private String phoneNumber; // 휴대폰 번호
    private String nickName; // 닉네임
    private String insDt; // 입력 일시
    private Integer userPoint; // 총 포인트
    private String userType; // 계정 타입
    private String profileName; // 프로필 이미지 이름
    private String profilePath; // 프로필 이미지 경로
    private String errorTxt;
    private Integer cnt; // ID와 닉네임이 존재하는지 체크
    
    
    // 즐겨찾기
    private String favorite1; // 즐겨찾기1 코드
    private String favoriteName1; // 즐겨찾기1 이름
    private String favorite2; // 즐겨찾기2 코드
    private String favoriteName2; // 즐겨찾기2 이름
    private String favorite3; // 즐겨찾기3 코드
    private String favoriteName3; // 즐겨찾기3 이름
    private String favorite4; // 즐겨찾기4 코드
    private String favoriteName4; // 즐겨찾기4 이름
    private String favorite5; // 즐겨찾기5 코드
    private String favoriteName5; // 즐겨찾기5 이름
    private String favorite6; // 즐겨찾기6 코드
    private String favoriteName6; // 즐겨찾기6 이름
    private String favorite7; // 즐겨찾기7 코드
    private String favoriteName7; // 즐겨찾기7 이름
    private String favorite8; // 즐겨찾기8 코드
    private String favoriteName8; // 즐겨찾기8 이름
    private String favorite9; // 즐겨찾기9 코드
    private String favoriteName9; // 즐겨찾기9 이름
    private String favorite10; // 즐겨찾기10 코드
    private String favoriteName10; // 즐겨찾기10 이름

}
