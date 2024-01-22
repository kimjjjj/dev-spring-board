package hello.dev.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class Board {

    private int seq; // seq
    private int beforeSeq; // 이전 seq
    private int afterSeq; // 다음 seq
    private String boardNumber; // 게시판 코드
    private String boardName; // 게시판명
    private int categoryNumber; // 카테고리 코드
    private String categoryName; // 카테고리명
    private String txtName; // 제목
    private String comment; // 내용
    private String tag; // 태그
    private String nickName; // 닉네임
    private String profileName; // 프로필 이미지 이름
    private String insId; // 입력자 ID
    private String insDt; // 입력 일시
    private String uptId; // 수정자 ID
    private String uptDt; // 수정 일시
    private Integer view; // 글 본 횟수
    private Integer point; // 추천 횟수
    private Integer cnt; // 좋아요 유무
    private Integer scrapCnt; // 스크랩 유무
    private String title; // view의 게시판 이름
    private String titleCode; // view의 게시판 코드
    private String searchKeyword; // 검색
    private String searchType; // 검색 콤보박스
    private String blockId; // 차단한 ID
    private Boolean noticeYn; // 공지 유무
    private Boolean imgyn; // 이미지 유무

    // 게시판, 카테고리 select박스
    private String boardCode; // 게시판 코드
    private String categoryCode; // 카테고리 코드

    private String saveFileName; // 저장 파일명
    private String path; // 저장 경로
    private Integer commentSeq; // 댓글 seq

    // 최근방문게시판
    private String visitBoard1; // 최근방문게시판1 코드
    private String visitBoardName1; // 최근방문게시판1 이름
    private String visitBoard2; // 최근방문게시판2 코드
    private String visitBoardName2; // 최근방문게시판2 이름
    private String visitBoard3; // 최근방문게시판3 코드
    private String visitBoardName3; // 최근방문게시판3 이름
    private String visitBoard4; // 최근방문게시판4 코드
    private String visitBoardName4; // 최근방문게시판4 이름
    private String visitBoard5; // 최근방문게시판5 코드
    private String visitBoardName5; // 최근방문게시판5 이름
    private String visitBoard6; // 최근방문게시판6 코드
    private String visitBoardName6; // 최근방문게시판6 이름
    private String visitBoard7; // 최근방문게시판7 코드
    private String visitBoardName7; // 최근방문게시판7 이름
    private String visitBoard8; // 최근방문게시판8 코드
    private String visitBoardName8; // 최근방문게시판8 이름
    private String visitBoard9; // 최근방문게시판9 코드
    private String visitBoardName9; // 최근방문게시판9 이름
    private String visitBoard10; // 최근방문게시판10 코드
    private String visitBoardName10; // 최근방문게시판10 이름

    // 페이징
    private Integer page1; // 페이지 버튼1
    private Integer page2; // 페이지 버튼2
    private Integer page3; // 페이지 버튼3
    private Integer page4; // 페이지 버튼4
    private Integer page5; // 페이지 버튼5
    private Integer currentPage; // 현재 페이지
    private Integer pageGroup; // 다음 페이지

    // 마이페이지 제목
    private String mypageTitle;

    // 유저페이지 제목
    private String userPageTitle;

    public Board() {
    }

    public Board(String boardNumber, String boardName, int categoryNumber, String categoryName, String txtName, String comment
            , String tag, String insId, String insDt, String uptId, String uptDt, Integer view, Integer point) {
        this.boardNumber = boardNumber;
        this.boardName = boardName;
        this.categoryNumber = categoryNumber;
        this.categoryName = categoryName;
        this.txtName = txtName;
        this.comment = comment;
        this.tag = tag;
        this.insId = insId;
        this.insDt = insDt;
        this.uptId = uptId;
        this.uptDt = uptDt;
        this.view = view;
        this.point = point;
    }
}