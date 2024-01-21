package hello.dev.service;

import hello.dev.domain.Board;
import hello.dev.domain.Member;
import hello.dev.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService implements BoardServiceInterface {

    private final BoardRepository boardRepository;

    // 한페이지에 보여줄 글 개수
    int pageLimit = 5;

    // 침하하 게시판
    @Override
    public List<Board> chimList(Integer page, String userId) {
        log.info("<=====BoardService.chimList=====>");

        // min <= 게시글 수 <= max
        int max = page * pageLimit;
        int min = max - (pageLimit - 1);

        // 게시글 조회
        List<Board> chimList = boardRepository.findChimList(min, max, userId);

        // 썸네일 작업
        Pattern nonValidPattern = Pattern.compile("(?i)< *[IMG][^\\>]*[src] *= *[\"\']{0,1}([^\"\'\\ >]*)");

        for (int i=0; i<chimList.size(); i++) {
            Board board = chimList.get(i);

            // 현재시간부터 글 작성시간 까지 계산
            setDteTime(board);

            // 타이틀 코드 세팅
            board.setTitleCode("chimhaha");

            // 게시판 코드->이름 작업
            board.setBoardName(boardCodeSet(false).get(board.getBoardCode()));

            // 카테고리 코드->이름 작업
            board.setCategoryName(categoryCodeSet().get(Integer.valueOf(board.getCategoryCode())));

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
    @Override
    public List<Board> boardList(String titleCode, Integer page, String userId) {
        log.info("<=====BoardService.boardList=====>");

        // min <= 게시글 수 <= max
        int max = page * pageLimit;
        int min = max - (pageLimit - 1);

        List<Board> boardList = boardRepository.boardList(titleCode, min, max, userId);

        // 썸네일 작업
        Pattern nonValidPattern = Pattern.compile("(?i)< *[IMG][^\\>]*[src] *= *[\"\']{0,1}([^\"\'\\ >]*)");

        for (int i=0; i<boardList.size(); i++) {
            Board board = boardList.get(i);

            // 현재시간부터 글 작성시간 까지 계산
            setDteTime(board);

            // 타이틀 코드 세팅
            board.setTitleCode(titleCode);

            // 게시판 코드->이름 작업
            board.setBoardName(boardCodeSet(false).get(board.getBoardCode()));

            // 카테고리 코드->이름 작업
            board.setCategoryName(categoryCodeSet().get(Integer.valueOf(board.getCategoryCode())));

            Matcher matcher = nonValidPattern.matcher(board.getComment());

            // 이미지 있으면 첫 이지미 뽑아내서 path에 넣음
            while (matcher.find()) {
                String img = matcher.group(1);
                board.setPath(img);
                break;
            }

            boardList.set(i, board);
        }

        return boardList;
    }

    // 게시글 조회
    @Override
    public Board boardPost(String userId, int seq, String titleCode) {
        log.info("<=====BoardService.boardPost=====>");

        // 게시글 뷰수 plus
        boardRepository.updateView(seq);

        // 게시글 조회
        Board board = boardRepository.findPost(userId, seq, titleCode);

        // 현재시간부터 글 작성시간 까지 계산
        setDteTime(board);

        // 타이틀 코드 세팅
        board.setTitleCode(titleCode);

        // 게시판 코드->이름 작업
        board.setBoardName(boardCodeSet(false).get(board.getBoardCode()));

        // 카테고리 코드->이름 작업
        board.setCategoryName(categoryCodeSet().get(Integer.valueOf(board.getCategoryCode())));

        if (!"".equals(board.getTag()) && board.getTag() != "" && board.getTag() != null) {
            // 태그에 #붙임
            String tag = board.getTag().replace(" ", "#").replace(",", "#").replace("##", "#").replace("#", " #");
            board.setTag("#" + tag);
        } else {
            board.setTag("");
        }

        return board;
    }

    @Override
    public Board save(Board board) {
        log.info("<=====BoardService.save=====>");

        return boardRepository.save(board);
    }

    @Override
    public Board saveImg(Board board, List<String > imgList) {
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
    @Override
    public Board editBoardPost(String userId, int seq, String titleCode) {
        log.info("<=====BoardService.editBoardPost=====>");

        // 게시글 조회
        Board board = boardRepository.findPost(userId, seq, titleCode);

        // 타이틀 코드 세팅
        board.setTitleCode(titleCode);

        // 게시판 코드->이름 작업
        board.setBoardName(boardCodeSet(false).get(board.getBoardCode()));

        // 카테고리 코드->이름 작업
        board.setCategoryName(categoryCodeSet().get(Integer.valueOf(board.getCategoryCode())));

        return board;
    }

    // 게시글 수정
    @Override
    public void updatePost(Board board, String userId) {
        log.info("<=====BoardService.updatePost=====>");

        // 게시글 update
        boardRepository.updatePost(board, userId);
    }

    // 게시글 삭제
    @Override
    public void deletePost(Integer seq) {
        log.info("<=====BoardService.deletePost=====>");

        // 게시글 update
        boardRepository.deletePost(seq);
    }

    // 첨부파일 삭제
    @Override
    public void deleteImg(Integer seq) {
        log.info("<=====BoardService.deleteImg=====>");

        // 게시글 update
        boardRepository.deleteImg(seq);
    }

    // 게시글 삭제 시 좋아요 삭제
    @Override
    public void deleteLike(Integer seq) {
        log.info("<=====BoardService.deleteLike=====>");

        // 좋아요 삭제
        boardRepository.deleteLike(seq);
    }

    // 게시글 포인트 plus
    @Override
    public void updateLike(int seq) {
        log.info("<=====BoardService.updateLike=====>");

        boardRepository.updateLike(seq);
    }

    // 게시글 포인트 minus
    @Override
    public void cancelLike(int seq) {
        log.info("<=====BoardService.cancelLike=====>");

        boardRepository.cancelLike(seq);
    }

    // 검색
    @Override
    public List<Board> search(String searchKeyword, String searchType, String userId) {
        log.info("<=====BoardService.search=====>");

        List<Board> boards = boardRepository.search(searchKeyword, searchType, userId);

        for (int i=0; i<boards.size(); i++) {
            Board board = boards.get(i);

            // 현재시간부터 글 작성시간 까지 계산
            setDteTime(board);

            // 게시판 코드->이름 작업
            board.setBoardName(boardCodeSet(false).get(board.getBoardCode()));
            boards.set(i, board);
        }

        return boards;
    }

    // 최근방문게시판(쿠키) 저장
    @Override
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
    @Override
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

    @Override
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
    @Override
    public String mypageTitle(String mypageTitle) {
        log.info("<=====BoardService.mypageTitle=====>");

        Map<String, String> map = new HashMap<>();
        map.put("post", "내가 쓴 글");
        map.put("comment", "내가 쓴 댓글");
        map.put("likePost", "좋아요 한 글");
        map.put("likeComment", "좋아요 한 댓글");
        map.put("scrap", "스크랩 한 글");
        map.put("block", "차단한 사용자");

        return map.get(mypageTitle);
    }

    // 마이페이지 조회
    @Override
    public List<Board> mywrite(String userId, String mypageTitle) {
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
        } else if ("scrap".equals(mypageTitle)) {
            boards = boardRepository.mypageScrap(userId);
        } else if ("block".equals(mypageTitle)) {
            boards = boardRepository.mypageBlock(userId);
        }

        for (int i=0; i<boards.size(); i++) {
            boards.get(i).setInsDt(boards.get(i).getInsDt().substring(5, 7) + "." + boards.get(i).getInsDt().substring(8, 10));
        }

        return boards;
    }

    // 현재시간부터 글 작성시간 까지 계산
    @Override
    public Board setDteTime(Board board) {
        log.info("<=====BoardService.setDteTime=====>");

        // 날짜 계산
        LocalDateTime nowDateTime = LocalDateTime.now();

        String strDate = board.getInsDt();
        String[] dateArr = strDate.substring(0, strDate.indexOf(" ")).split("-");

        String[] timeArr = null;
        if (strDate.indexOf(".") != -1) {
            timeArr = strDate.substring(strDate.indexOf(" ") + 1, strDate.indexOf(".")).split(":");
        } else {
            timeArr = strDate.substring(strDate.indexOf(" ") + 1, strDate.length()).split(":");
        }

        LocalDateTime boardDateTime = LocalDateTime.of(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]),
                Integer.parseInt(dateArr[2]), Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), Integer.parseInt(timeArr[2]));
        Duration duration = Duration.between(boardDateTime, nowDateTime);

        long day = duration.getSeconds()/(60*60)/24;
        long hour = duration.getSeconds()/(60*60);
        long minute = duration.getSeconds()/60;
        long second = duration.getSeconds();

        if (168 < hour) {
            board.setInsDt(Integer.parseInt(dateArr[1]) + "." + Integer.parseInt(dateArr[2]));
        } else if (24 <= hour && hour <= 168) {
            board.setInsDt(day + "일전");
        } else if (hour > 0) {
            board.setInsDt(hour + "시간전");
        } else if (minute > 0) {
            board.setInsDt(minute + "분전");
        } else {
            board.setInsDt(second + "초전");
        }

        return board;
    }

    // 유저게시글 제목
    @Override
    public String userPageTitle(String userPageTitle) {
        log.info("<=====BoardService.userPageTitle=====>");

        Map<String, String> map = new HashMap<>();
        map.put("post", "의 게시글");
        map.put("comment", "의 댓글");

        return map.get(userPageTitle);
    }
    
    // 유저 게시글, 댓글 조회
    @Override
    public List<Board> userWrite(String nickName, String userPageTitle) {
        log.info("<=====BoardService.userWrite=====>");

        List<Board> boards = new ArrayList<>();

        if ("post".equals(userPageTitle)) {
            boards = boardRepository.userPagePost(nickName);
        } else if ("comment".equals(userPageTitle)) {
            boards = boardRepository.userPageComment(nickName);
        }

        for (int i=0; i<boards.size(); i++) {

            if ("post".equals(userPageTitle)) {
                // 게시판 코드->이름 작업
                boards.get(i).setBoardName(boardCodeSet(false).get(boards.get(i).getBoardCode()));

                // 카테고리 코드->이름 작업
                boards.get(i).setCategoryName(categoryCodeSet().get(Integer.valueOf(boards.get(i).getCategoryCode())));
            }

            boards.get(i).setInsDt(boards.get(i).getInsDt().substring(5, 7) + "." + boards.get(i).getInsDt().substring(8, 10));
        }

        return boards;
    }

    // 게시판 코드, 이름 세팅
    @Override
    public Map<String, String> boardCodeSet(Boolean use) {
        log.info("<=====BoardService.boardCodeSet=====>");

        Map<String, String> codeMap = new HashMap<>();

        codeMap.put("all", "전체게시글");
        codeMap.put("total_chim", "침착맨 전체게시글");
        codeMap.put("total_chimtubu", "침투부 전체게시글");

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
    @Override
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
        codeMap.put(15, "신고");
        codeMap.put(16, "건의");
        codeMap.put(17, "버그제보");
        codeMap.put(998, "방송일정");
        codeMap.put(999, "공지사항");

        return codeMap;
    }
}
