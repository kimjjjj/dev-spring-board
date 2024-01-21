package hello.dev.service;

import hello.dev.domain.Board;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface BoardServiceInterface {

    List<Board> chimList(Integer page, String userId);

    List<Board> boardList(String titleCode, Integer page, String userId);

    Board boardPost(String userId, int seq, String titleCode);

    Board save(Board board);

    Board saveImg(Board board, List<String > imgList);

    Board editBoardPost(String userId, int seq, String titleCode);

    void updatePost(Board board, String userId);

    void deletePost(Integer seq);

    void deleteImg(Integer seq);

    void deleteLike(Integer seq);

    void updateLike(int seq);

    void cancelLike(int seq);

    List<Board> search(String searchKeyword, String searchType, String userId);

    void saveCookie(String titleCode, HttpServletRequest request, HttpServletResponse response);

    Board getCookie(Board board, HttpServletRequest request, String titleCode);

    Board setPage(Board board, Integer page);

    String mypageTitle(String mypageTitle);

    List<Board> mywrite(String userId, String mypageTitle);

    Board setDteTime(Board board);

    Map<String, String> boardCodeSet(Boolean use);

    Map<Integer, String> categoryCodeSet();

    String userPageTitle(String userPageTitle);

    List<Board> userWrite(String nickName, String userPageTitle);
}
