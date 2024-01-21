package hello.dev.repository;

import hello.dev.domain.Comment;

import java.util.List;

public interface CommentRepositoryInterface {

    List<Comment> findComment(String userId, Integer seq);

    void saveParentComment(Comment comment, String userId, Integer boardSeq, String content
            , Integer lvl, Integer orderRow);

    void saveChildComment(Comment comment, String userId, Integer boardSeq, String content, Integer topSeq
            , Integer parentSeq, Integer lvl, Integer orderRow);

    void editComment(Integer seq, String content);

    void deleteComment(Integer seq);

    void deleteCommentBoard(Integer seq);

    Integer chkComment(Integer topSeq, Integer orderRow);

    void updateComment(Integer topSeq, Integer orderRow);

    void commentLike(String userId, Integer seq);

    void updateCommentPoint(int seq);

    void commentCancel(String userId, int seq);

    void cancelCommentPoint(int seq);
}
