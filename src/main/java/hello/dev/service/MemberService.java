package hello.dev.service;

import hello.dev.domain.Block;
import hello.dev.domain.Member;
import hello.dev.repository.BoardRepository;
import hello.dev.repository.CommentRepository;
import hello.dev.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    // 첨부파일 저장 경로
    @Value("${upload.path}")
    private String filePath;

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

    private final MemberRepository memberRepository;
    private final BoardService boardService;
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final LoginService loginService;

    // 회원가입 저장
    public Member save(Member member) {
        log.info("<=====MemberService.save=====>");

        return memberRepository.save(member);
    }

    // 휴대폰번호 '-' 세팅
    public String phoneSetting(String phone) {
        log.info("<=====MemberService.phoneSetting=====>");

        // 번호에 '-' 세팅 ex) 01012345678 -> 010-1234-5678
        return phone.substring(0, 3) + '-' + phone.substring(3, 7) + '-' + phone.substring(7, 11);
    }

    // 회원가입 시 에러 체크
    public Map<String, String> checkError(Member member) {
        log.info("<=====MemberService.checkError=====>");

        Map<String, String> errors = new HashMap<>();
        
        // 아이디
        if (!StringUtils.hasText(member.getUserId())) {
            errors.put("userId", "아이디: 필수 정보입니다.");
        } else {
            Integer cnt = memberRepository.findByIdOrNick("ID", member.getUserId());

            if (cnt == 1) {
                errors.put("userId", "아이디: 중복입니다.");
            }

            if (member.getUserId().length() < 5) {
                errors.put("userId", "아이디: 5~20자의 영문 소문자, 숫자만 사용 가능합니다.");
            }
        }

        // 비밀번호
        if (!StringUtils.hasText(member.getPassword())) {
            errors.put("password", "비밀번호: 필수 정보입니다.");
        } else {
            if (member.getPassword().length() < 8) {
                errors.put("password", "비밀번호: 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해 주세요.");
            }
        }

        // 이름
        if (!StringUtils.hasText(member.getUserName())) {
            errors.put("userName", "이름: 필수 정보입니다.");
        }

        // 생년월일
        if (!StringUtils.hasText(String.valueOf(member.getBirth()))) {
            errors.put("birth", "생년월일: 필수 정보입니다.");
        } else {
            if (member.getBirth().length() < 8) {
                errors.put("birth", "생년월일: 8자리 숫자로 입력해 주세요.");
            }
        }

        // 휴대전화번호
        if (!StringUtils.hasText(String.valueOf(member.getPhoneNumber()))) {
            errors.put("phoneNumber", "휴대전화번호: 필수 정보입니다.");
        } else {
            if (member.getPhoneNumber().length() < 11) {
                errors.put("phoneNumber", "휴대전화번호: 11 자리 숫자로 입력해 주세요.");
            } else {
                if (!"010".equals(member.getPhoneNumber().substring(0, 3))) {
                    errors.put("phoneNumber", "휴대전화번호: 정확한지 확인해 주세요.");
                }
            }
        }

        // 닉네임
        if (!StringUtils.hasText(member.getNickName())) {
            errors.put("nickName", "닉네임: 필수 정보입니다.");
        } else {
            Integer cnt = memberRepository.findByIdOrNick("NICKNAME", member.getNickName());

            if (cnt == 1) {
                errors.put("nickName", "닉네임: 중복입니다.");
            }
        }

        return errors;
    }

    // 닉네임 변경 시 에러 체크
    public Map<String, String> checkNick(String nickName) {
        log.info("<=====MemberService.checkNick=====>");

        Map<String, String> errors = new HashMap<>();

        if ("".equals(nickName)) {
            errors.put("nickName", "닉네임: 필수 정보입니다.");

            return errors;
        }

        Integer cnt = memberRepository.findByIdOrNick("NICKNAME", nickName);

        if (cnt == 1) {
            errors.put("nickName", "닉네임: 중복입니다.");
        }

        return errors;
    }

    // ID 중복 체크
    public Integer checkId(String userId) {
        log.info("<=====MemberService.checkId=====>");

        return memberRepository.findByIdOrNick("ID", userId);
    }

    // 계정의 포인트 찾기
    public Integer findByUserPoint(String userId) {
        log.info("<=====MemberService.findByUserPoint=====>");

        return memberRepository.findByUserPoint(userId);
    }

    // 유저 포인트 plus
    public void updateUserPoint(String userId, int seq) {
        log.info("<=====MemberService.updateUserPoint=====>");

        memberRepository.updateUserPoint(userId, seq);
    }

    // 유저 포인트 minus
    public void cancelUserPoint(String userId, int seq) {
        log.info("<=====MemberService.cancelUserPoint=====>");

        memberRepository.cancelUserPoint(userId, seq);
    }

    // 스크랩 저장
    public void scrapSave(String userId, int seq) {
        log.info("<=====MemberService.scrapSave=====>");

        memberRepository.scrapSave(userId, seq);
    }

    // 스크랩 취소
    public void scrapCancel(String userId, int seq) {
        log.info("<=====MemberService.scrapCancel=====>");

        memberRepository.scrapCancel(userId, seq);
    }

    // 게시판 즐겨찾기
    public Member addFavorite(String userId, String titleCode, Member member) {
        log.info("<=====MemberService.addFavorite=====>");

        // DB insert
        memberRepository.addFavorite(userId, titleCode);

        // 즐겨찾기 코드->이름 변환map
        Map<String, String> favoriteMap = boardService.boardCodeSet(false);

        if (member.getFavorite1() == null) {
            member.setFavorite1(titleCode);
            member.setFavoriteName1(favoriteMap.get(member.getFavorite1()));
        } else if (member.getFavorite2() == null) {
            member.setFavorite2(titleCode);
            member.setFavoriteName2(favoriteMap.get(member.getFavorite2()));
        } else if (member.getFavorite3() == null) {
            member.setFavorite3(titleCode);
            member.setFavoriteName3(favoriteMap.get(member.getFavorite3()));
        } else if (member.getFavorite4() == null) {
            member.setFavorite4(titleCode);
            member.setFavoriteName4(favoriteMap.get(member.getFavorite4()));
        } else if (member.getFavorite5() == null) {
            member.setFavorite5(titleCode);
            member.setFavoriteName5(favoriteMap.get(member.getFavorite5()));
        } else if (member.getFavorite6() == null) {
            member.setFavorite6(titleCode);
            member.setFavoriteName6(favoriteMap.get(member.getFavorite6()));
        } else if (member.getFavorite7() == null) {
            member.setFavorite7(titleCode);
            member.setFavoriteName7(favoriteMap.get(member.getFavorite7()));
        } else if (member.getFavorite8() == null) {
            member.setFavorite8(titleCode);
            member.setFavoriteName8(favoriteMap.get(member.getFavorite8()));
        } else if (member.getFavorite9() == null) {
            member.setFavorite9(titleCode);
            member.setFavoriteName9(favoriteMap.get(member.getFavorite9()));
        } else if (member.getFavorite10() == null) {
            member.setFavorite10(titleCode);
            member.setFavoriteName10(favoriteMap.get(member.getFavorite10()));
        }

        return member;
    }
    
    // 게시판 즐겨찾기 해제
    public Member removeFavorite(String userId, String titleCode, Member member) {
        log.info("<=====MemberService.removeFavorite=====>");

        // DB delete
        memberRepository.removeFavorite(userId, titleCode);

        // 게시판 즐겨찾기 map
        Map<Integer, String> favMap = favoriteMap(member);

        /**
         * ex) 게시판 즐겨찾기 중 세번째 게시판이 즐겨찾기 해제 된다면
         * 4번째 게시판 -> 3번째로 옮김
         * 5번째 게시판 -> 4번째 옮김
         * ...
         */
        for (int i=1; i<=10; i++) {

            if (titleCode.equals(favMap.get(i))) {
                member.setFavorite3("");

                for (int j=i+1; j<=10; j++) {
                    favMap.put(j-1, favMap.get(j));
                }

                break;
            }
        }

        member.setFavorite1(favMap.get(1));
        member.setFavorite2(favMap.get(2));
        member.setFavorite3(favMap.get(3));
        member.setFavorite4(favMap.get(4));
        member.setFavorite5(favMap.get(5));
        member.setFavorite6(favMap.get(6));
        member.setFavorite7(favMap.get(7));
        member.setFavorite8(favMap.get(8));
        member.setFavorite9(favMap.get(9));
        member.setFavorite10(favMap.get(10));

        // 즐겨찾기 코드->이름 변환
        Map<String, String> favoriteMap = boardService.boardCodeSet(false);
        member.setFavoriteName1(favoriteMap.get(member.getFavorite1()));
        member.setFavoriteName2(favoriteMap.get(member.getFavorite2()));
        member.setFavoriteName3(favoriteMap.get(member.getFavorite3()));
        member.setFavoriteName4(favoriteMap.get(member.getFavorite4()));
        member.setFavoriteName5(favoriteMap.get(member.getFavorite5()));
        member.setFavoriteName6(favoriteMap.get(member.getFavorite6()));
        member.setFavoriteName7(favoriteMap.get(member.getFavorite7()));
        member.setFavoriteName8(favoriteMap.get(member.getFavorite8()));
        member.setFavoriteName9(favoriteMap.get(member.getFavorite9()));
        member.setFavoriteName10(favoriteMap.get(member.getFavorite10()));

        return member;
    }

    // 게시판 즐겨찾기 조회
    public Member favoriteList(Member member, String userId) {
        log.info("<=====MemberService.favoriteList=====>");

        member = memberRepository.favoriteList(member, userId);

        // 즐겨찾기 코드->이름 변환
        Map<String, String> favoriteMap = boardService.boardCodeSet(false);

        member.setFavoriteName1(favoriteMap.get(member.getFavorite1()));
        member.setFavoriteName2(favoriteMap.get(member.getFavorite2()));
        member.setFavoriteName3(favoriteMap.get(member.getFavorite3()));
        member.setFavoriteName4(favoriteMap.get(member.getFavorite4()));
        member.setFavoriteName5(favoriteMap.get(member.getFavorite5()));
        member.setFavoriteName6(favoriteMap.get(member.getFavorite6()));
        member.setFavoriteName7(favoriteMap.get(member.getFavorite7()));
        member.setFavoriteName8(favoriteMap.get(member.getFavorite8()));
        member.setFavoriteName9(favoriteMap.get(member.getFavorite9()));
        member.setFavoriteName10(favoriteMap.get(member.getFavorite10()));

        return member;
    }

    // 게시판 즐겨찾기 map
    public Map<Integer, String> favoriteMap (Member member) {
        log.info("<=====MemberService.favoriteMap=====>");

        Map<Integer, String> map = new HashMap<>();

        map.put(1, member.getFavorite1());
        map.put(2, member.getFavorite2());
        map.put(3, member.getFavorite3());
        map.put(4, member.getFavorite4());
        map.put(5, member.getFavorite5());
        map.put(6, member.getFavorite6());
        map.put(7, member.getFavorite7());
        map.put(8, member.getFavorite8());
        map.put(9, member.getFavorite9());
        map.put(10, member.getFavorite10());

        return map;
    }

    // 회원정보 update
    public Member saveMypage(Member member, String userId, String nickName, String profileName, String profilePath) {
        log.info("<=====MemberService.saveMypage=====>");

        memberRepository.saveMypage(userId, nickName, profileName, profilePath);

        member.setNickName(nickName);

        return member;
    }

    // 파일을 경로에 저장
    public void setProfile(String uploadFileName, HttpServletRequest request) throws ServletException, IOException {
        log.info("<=====MemberService.setProfile=====>");

        Collection<Part> parts = request.getParts();

        for (Part part : parts) {
            part.write(uploadFileName);
            break;
        }
    }

    // 회원탈퇴
    public void delete(String userId, HttpSession session) throws IOException {
        log.info("<=====MemberService.delete=====>");

        // 네이버 로그인 체크
        int cntId = checkId(userId);

        // 네이버 로그인 계정이면 연결 삭제
        if (cntId != 0) {
            String apiURL = url + "token?grant_type=delete&";
            apiURL += "client_id=" + client_id;
            apiURL += "&client_secret=" + client_secret;
            apiURL += "&access_token=" + session.getAttribute("currentAT");
            apiURL += "&service_provider=NAVER";

            loginService.requestToServer(apiURL, "");
        }

        boardRepository.deleteLikeById(userId); // 좋아요 테이블 삭제
        memberRepository.deleteFavorite(userId); // 즐겨찾기 테이블 삭제
        memberRepository.deleteBlock(userId); // 유저차단 테이블 삭제
        commentRepository.deleteCommentById(userId); // 댓글 테이블 삭제
        boardRepository.deleteAttachById(userId); // 첨부파일 테이블 삭제
        boardRepository.deleteBoardById(userId); // 게시글 테이블 삭제
        memberRepository.deleteMember(userId); // 계정 테이블 삭제
    }

    // 사용자 차단
    public void addBlock(Block block, String userId, String boardId) {
        log.info("<=====MemberService.addBlock=====>");

        memberRepository.addBlock(block, userId, boardId);
    }

    // 사용자 차단해제
    public void cancelBlock(String userId, String blockId) {
        log.info("<=====MemberService.cancelBlock=====>");

        memberRepository.cancelBlock(userId, blockId);
    }
}
