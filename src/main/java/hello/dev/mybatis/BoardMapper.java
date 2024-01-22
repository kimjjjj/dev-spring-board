package hello.dev.mybatis;

import hello.dev.domain.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<Board> findChimList(@Param("min") Integer min, @Param("max") Integer max, @Param("userId") String userId);

    List<Board> boardList(@Param("titleCode") String titleCode, @Param("min") Integer min
            , @Param("max") Integer max, @Param("userId") String userId);

    Board findPost(@Param("userId") String userId, @Param("seq") int seq, @Param("titleCode") String titleCode);

    void updateView(int seq);

    void save(Board board);

    void saveImg(Board board);

    void updatePost(@Param("board") Board board, @Param("userId") String userId);

    void deletePost(Integer seq);

    void deleteImg(Integer seq);

    void deleteLike(Integer seq);

    void updateLike(int seq);

    void cancelLike(int seq);

    List<Board> search(@Param("searchKeyword") String searchKeyword
            , @Param("searchType") String searchType, @Param("userId") String userId);

    List<Board> mypagePost(String userId);

    List<Board> mypageComment(String userId);

    List<Board> mypageLikePost(String userId);

    List<Board> mypageLikeComment(String userId);

    List<Board> mypageScrap(String userId);

    List<Board> mypageBlock(String userId);

    List<Board> userPagePost(String nickName);

    List<Board> userPageComment(String nickName);

    void deleteBoardById(String userId);

    void deleteAttachById(String userId);

    void deleteLikeById(String userId);
}
