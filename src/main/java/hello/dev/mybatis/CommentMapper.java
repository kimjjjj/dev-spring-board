package hello.dev.mybatis;

import hello.dev.domain.Comment;
import hello.dev.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> findComment(@Param("userId") String userId, @Param("seq") Integer seq);

    void saveParentComment(@Param("comment") Comment comment, @Param("userId") String userId, @Param("boardSeq") Integer boardSeq, @Param("content") String content
            , @Param("lvl") Integer lvl, @Param("orderRow") Integer orderRow);

    void saveChildComment(@Param("comment") Comment comment, @Param("userId") String userId, @Param("boardSeq") Integer boardSeq, @Param("content") String content
            , @Param("topSeq") Integer topSeq, @Param("parentSeq") Integer parentSeq, @Param("lvl") Integer lvl, @Param("orderRow") Integer orderRow);

    void editComment(@Param("seq") Integer seq, @Param("content") String content);

    void deleteComment(Integer seq);

    void deleteCommentBoard(Integer seq);

    Integer chkComment(@Param("topSeq") Integer topSeq, @Param("orderRow") Integer orderRow);

    void updateComment(@Param("topSeq") Integer topSeq, @Param("orderRow") Integer orderRow);

    void commentLike(@Param("userId") String userId, @Param("seq") Integer seq);

    void updateCommentPoint(int seq);

    void commentCancel(@Param("userId") String userId, @Param("seq") int seq);

    void cancelCommentPoint(int seq);

    void deleteCommentById(String userId);
}
