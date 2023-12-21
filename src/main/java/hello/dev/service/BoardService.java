package hello.dev.service;

import hello.dev.domain.Board;
import hello.dev.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // 한페이지에 보여줄 글 개수
    int pageLimit = 5;

    // 침하하 게시판
    public List<Board> chimList(Integer page) throws SQLException {
        log.info("<=====BoardService.chimList=====>");

        // min <= 게시글 수 <= max
        int max = page * pageLimit;
        int min = max - (pageLimit - 1);

        // 게시글 조회
        List<Board> chimList = boardRepository.findChimList(min, max);

        // 썸네일 작업
        Pattern nonValidPattern = Pattern.compile("(?i)< *[IMG][^\\>]*[src] *= *[\"\']{0,1}([^\"\'\\ >]*)");

        for (int i=0; i<chimList.size(); i++) {
            Board board = chimList.get(i);

            board.setTitleCode("chimhaha"); // 타이틀 코드 세팅

            board.setBoardName(boardCodeSet(false).get(board.getBoardNumber()));
            Matcher matcher = nonValidPattern.matcher(board.getComment());
            
            // 이미지 있으면 첫 이지미 뽑아내서 path에 넣음
            while (matcher.find()) {
                String img = matcher.group(1);
                board.setPath(img);
                break;
            }

            chimList.set(i, board);
        }

        return chimList;
    }

    // 침하하 제외한 게시판
    public List<Board> boardList(String titleCode, Integer page) throws SQLException {
        log.info("<=====BoardService.boardList=====>");

        // min <= 게시글 수 <= max
        int max = page * pageLimit;
        int min = max - (pageLimit - 1);

        List<Board> boardList = boardRepository.boardList(titleCode, min, max);

        // 썸네일 작업
        Pattern nonValidPattern = Pattern.compile("(?i)< *[IMG][^\\>]*[src] *= *[\"\']{0,1}([^\"\'\\ >]*)");

        for (int i=0; i<boardList.size(); i++) {
            Board board = boardList.get(i);
            board.setBoardName(boardCodeSet(false).get(board.getBoardNumber()));
            Matcher matcher = nonValidPattern.matcher(board.getComment());

            // 이미지 있으면 첫 이지미 뽑아내서 path에 넣음
            while (matcher.find()) {
                String img = matcher.group(1);
                board.setPath(img);
                break;
            }

            boardList.set(i, board);
        }

        // 게시판 코드->이름 작업
        for (int i=0; i<boardList.size(); i++) {
            Board board = boardList.get(i);

            board.setTitleCode(titleCode); // 타이틀 코드 세팅
            board.setBoardName(boardCodeSet(false).get(board.getBoardNumber()));
            boardList.set(i, board);
        }

        return boardList;
    }

    // 게시글 조회
    public Board boardPost(String userId, int seq, String titleCode) throws SQLException {
        log.info("<=====BoardService.boardPost=====>");

        // 게시글 뷰수 plus
        boardRepository.updateView(seq);

        // 게시글 조회
        Board board = boardRepository.findPost(userId, seq, titleCode);

        board.setTitleCode(titleCode);
        board.setBoardName(boardCodeSet(false).get(board.getBoardNumber()));
        board.setCategoryName(categoryCodeSet().get(board.getCategoryNumber()));

        // 태그에 #붙임
        String tag = board.getTag().replace(" ", "#").replace(",", "#").replace("##", "#").replace("#", " #");
        board.setTag("#" + tag);

        return board;
    }

    public Board save(Board board) throws SQLException {
        log.info("<=====BoardService.save=====>");

        return boardRepository.save(board);
    }

    public Board saveImg(Board board, List<String > imgList) throws SQLException {
        log.info("<=====BoardService.saveImg=====>");

        Pattern nonValidPattern = Pattern.compile("(?i)< *[IMG][^\\>]*[src] *= *[\"\']{0,1}([^\"\'\\ >]*)");

        for (String s : imgList) {
            Matcher matcher = nonValidPattern.matcher(s);

            while (matcher.find()) {
                String img = matcher.group(1);
                board.setPath(img.substring(0, img.indexOf("/", img.indexOf("/") + 1)+1));
                board.setSaveFileName(img.substring(img.indexOf("/", img.indexOf("/") + 1)+1, img.length()));
            }

            boardRepository.saveImg(board);
        }

//        int imgCnt = 0;
//        String img = "";
//
//        for (String s : imgList) {
//            Matcher matcher = nonValidPattern.matcher(s);
//            while (matcher.find()) {
//
//                img = matcher.group(1);
//                imgCnt++;
//
//                if (imgCnt == 1) {
//                    break;
//                }
//            }
//        }
//
//        img;
        return board;
    }

    // 게시글 수정 이동
    public Board editBoardPost(String userId, int seq, String titleCode) throws SQLException {
        log.info("<=====BoardService.editBoardPost=====>");

        // 게시글 뷰수 plus
        boardRepository.updateView(seq);

        // 게시글 조회
        Board board = boardRepository.findPost(userId, seq, titleCode);

        board.setTitleCode(titleCode);
        board.setBoardName(boardCodeSet(false).get(board.getBoardNumber()));
        board.setCategoryName(categoryCodeSet().get(board.getCategoryNumber()));

        return board;
    }

    // 게시글 수정
    public void updatePost(Board board, String userId) throws SQLException {
        log.info("<=====BoardService.updatePost=====>");

        // 게시글 update
        boardRepository.updatePost(board, userId);
    }

    // 게시글 삭제
    public void deletePost(Integer seq) throws SQLException {
        log.info("<=====BoardService.deletePost=====>");

        // 게시글 update
        boardRepository.deletePost(seq);
    }

    // 첨부파일 삭제
    public void deleteImg(Integer seq) throws SQLException {
        log.info("<=====BoardService.deleteImg=====>");

        // 게시글 update
        boardRepository.deleteImg(seq);
    }

    // 게시글 포인트 plus
    public String updateLike(int seq) throws SQLException {
        log.info("<=====BoardService.updateLike=====>");

        return boardRepository.updateLike(seq);
    }

    // 게시글 포인트 minus
    public String cancelLike(int seq) throws SQLException {
        log.info("<=====BoardService.cancelLike=====>");

        return boardRepository.cancelLike(seq);
    }

    // 검색
    public List<Board> search(String searchKeyword, String searchType) throws SQLException {
        log.info("<=====BoardService.search=====>");

        List<Board> boards = boardRepository.search(searchKeyword, searchType);

        for (int i=0; i<boards.size(); i++) {
            Board board = boards.get(i);
            board.setBoardName(boardCodeSet(false).get(board.getBoardNumber()));
            boards.set(i, board);
        }

        return boards;
    }

    // 최근방문게시판(쿠키) 저장
    public void saveCookie(String titleCode, HttpServletRequest request, HttpServletResponse response) {
        log.info("<=====BoardService.saveCookie=====>");

        Cookie[] cookies = request.getCookies();

        String cookieVal = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(titleCode)) {
                    cookieVal = cookie.getValue();
                    break;
                }
            }

            if (cookieVal != null) {
                Cookie cookie = new Cookie(cookieVal, null);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } else {

                if (cookies.length == 8) {
                    Cookie cookie = new Cookie(cookies[0].getName(), null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }

        Cookie cookie = new Cookie(titleCode, titleCode);
        cookie.setPath("/");
        cookie.setMaxAge(24*60*60);
        response.addCookie(cookie);
    }

    // 최근방문게시판(쿠키) 조회
    public Board getCookie(Board board, HttpServletRequest request, String titleCode) {
        log.info("<=====BoardService.getCookie=====>");

        Cookie[] cookies = request.getCookies();

        Map<Integer, String> map = new HashMap<>();

        if (cookies != null) {
            for (int i=0; i<cookies.length; i++) {
                if (titleCode != null) {
                    if (!titleCode.equals(cookies[i].getValue())) {
                        map.put(i, cookies[i].getValue());
                    }
                } else {
                    map.put(i, cookies[i].getValue());
                }
            }
        }

        Map<String, String> boardMap = boardCodeSet(false);

        if (titleCode != null) {
            board.setVisitBoard1(titleCode);
            board.setVisitBoardName1(boardMap.get(titleCode));
        } else {
            board.setVisitBoard2(map.get(7));
            board.setVisitBoardName2(boardMap.get(map.get(7)));
        }

        board.setVisitBoard4(map.get(6));
        board.setVisitBoardName4(boardMap.get(map.get(6)));

        board.setVisitBoard5(map.get(5));
        board.setVisitBoardName5(boardMap.get(map.get(5)));

        board.setVisitBoard6(map.get(4));
        board.setVisitBoardName6(boardMap.get(map.get(4)));

        board.setVisitBoard7(map.get(3));
        board.setVisitBoardName7(boardMap.get(map.get(3)));

        board.setVisitBoard8(map.get(2));
        board.setVisitBoardName8(boardMap.get(map.get(2)));

        board.setVisitBoard9(map.get(1));
        board.setVisitBoardName9(boardMap.get(map.get(1)));

        board.setVisitBoard10(map.get(0));
        board.setVisitBoardName10(boardMap.get(map.get(0)));

        return board;
    }

    public Board setPage(Board board, Integer page) {
        log.info("<=====BoardService.setPage=====>");

        int max = page;
        while(max % 5 != 0) {
            max++;
        }

        board.setPage1(max - 4);
        board.setPage2(max - 3);
        board.setPage3(max - 2);
        board.setPage4(max - 1);
        board.setPage5(max);
        board.setCurrentPage(page);

        return board;
    }

    // 마이페이지 제목
    public String mypageTitle(String mypageTitle) {
        log.info("<=====BoardService.mypageTitle=====>");

        Map<String, String> map = new HashMap<>();
        map.put("post", "내가 쓴 글");
        map.put("comment", "내가 쓴 댓글");
        map.put("likePost", "좋아요 한 글");
        map.put("likeComment", "좋아요 한 댓글");

        return map.get(mypageTitle);
    }

    // 마이페이지 조회
    public List<Board> mywrite(String userId, String mypageTitle) throws SQLException {
        log.info("<=====BoardService.mywrite=====>");

        List<Board> boards = new ArrayList<>();

        if ("post".equals(mypageTitle)) {
            boards = boardRepository.mypagePost(userId);
        } else if ("comment".equals(mypageTitle)) {
            boards = boardRepository.mypageComment(userId);
        } else if ("likePost".equals(mypageTitle)) {
            boards = boardRepository.mypageLikePost(userId);
        } else if ("likeComment".equals(mypageTitle)) {
            boards = boardRepository.mypageLikeComment(userId);
        }

        for (int i=0; i<boards.size(); i++) {
            boards.get(i).setInsDt(boards.get(i).getInsDt().substring(5, 7) + "." + boards.get(i).getInsDt().substring(8, 10));
        }

        return boards;
    }

    // 게시판 코드, 이름 세팅
    public Map<String, String> boardCodeSet(Boolean use) {
        log.info("<=====BoardService.boardCodeSet=====>");

        Map<String, String> codeMap = new HashMap<>();

        codeMap.put("all", "전체게시글");
        codeMap.put("total_chim", "침착맨 전체게시글");

        if (use) {
            codeMap.put("chimhaha", "\uD83D\uDC4D침하하");
            codeMap.put("library", "\uD83C\uDFDB️알렉산드리아 짤 도서관");

            codeMap.put("notice", "\uD83D\uDC40방송일정 및 공지");
            codeMap.put("chim", "\uD83D\uDE0A침착맨");
            codeMap.put("chim_jjal", "\uD83C\uDF83침착맨 짤");
            codeMap.put("chim_fanart", "\uD83C\uDFA8침착맨 팬아트");
            codeMap.put("request_stream", "\uD83D\uDCE3방송 해줘요");
            codeMap.put("find_chimtube", "\uD83C\uDF73침투부 찾아요");
            codeMap.put("make_short", "\uD83C\uDFAC쇼츠 만들어줘요");
            codeMap.put("favorite_chimtubu", "\uD83D\uDC53재밌게 본 침투부");
        } else {
            codeMap.put("chimhaha", "침하하");
            codeMap.put("library", "️알렉산드리아 짤 도서관");

            codeMap.put("chim", "침착맨");
            codeMap.put("notice", "방송일정 및 공지");
            codeMap.put("chim_jjal", "침착맨 짤");
            codeMap.put("chim_fanart", "침착맨 팬아트");
            codeMap.put("request_stream", "방송 해줘요");
            codeMap.put("find_chimtube", "침투부 찾아요");
            codeMap.put("make_short", "쇼츠 만들어줘요");
            codeMap.put("favorite_chimtubu", "재밌게 본 침투부");
        }

        return codeMap;
    }

    // 카테고리 코드, 이름 세팅
    public Map<Integer, String> categoryCodeSet() {
        log.info("<=====BoardService.categoryCodeSet=====>");

        Map<Integer, String> codeMap = new HashMap<>();

        codeMap.put(1, "침착맨");
        codeMap.put(2, "생중계");
        codeMap.put(3, "침착맨 짤");
        codeMap.put(4, "팬아트");
        codeMap.put(5, "팬무비");
        codeMap.put(6, "팬픽션");
        codeMap.put(7, "팬게임");
        codeMap.put(8, "게임");
        codeMap.put(9, "합방");
        codeMap.put(10, "개인컨텐츠");
        codeMap.put(11, "해줘요");
        codeMap.put(12, "찾아요");
        codeMap.put(13, "쇼츠");
        codeMap.put(14, "추천영상");

        return codeMap;
    }
}
