package hello.dev.repository;

import hello.dev.domain.Board;

import java.util.List;

public interface BoardRepositoryInterface {

    List<Board> findChimList(Integer min, Integer max, String userId);

    List<Board> boardList(String titleCode, Integer min, Integer max, String userId);

    Board findPost(String userId, int seq, String titleCode);

    void updateView(int seq);

    Board save(Board board);

    Board saveImg(Board board);

    void updatePost(Board board, String userId);

    void deletePost(Integer seq);

    void deleteImg(Integer seq);

    void deleteLike(Integer seq);

    void updateLike(int seq);

    void cancelLike(int seq);

    List<Board> search(String searchKeyword, String searchType, String userId);

    List<Board> mypagePost(String userId);

    List<Board> mypageComment(String userId);

    List<Board> mypageLikePost(String userId);

    List<Board> mypageLikeComment(String userId);

    List<Board> mypageScrap(String userId);

    List<Board> mypageBlock(String userId);

    List<Board> userPagePost(String boardId);

    List<Board> userPageComment(String boardId);
}
