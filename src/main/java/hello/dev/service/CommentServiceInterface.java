package hello.dev.service;

import hello.dev.domain.Comment;

import java.util.List;

public interface CommentServiceInterface {

    List<Comment> findComment(String userId, Integer seq);

    void saveParentComment(Comment comment, String userId, Integer boardSeq, String content
            , Integer lvl, Integer orderRow);

    void saveChildComment(Comment comment, String userId, Integer boardSeq, String content, Integer topSeq
            , Integer parentSeq, Integer lvl, Integer orderRow);

    void editComment(Integer seq, String content);

    void deleteComment(Integer seq);

    void deleteCommentBoard(Integer seq);

    void updateComment(Integer topSeq, Integer orderRow);

    void commentLike(String userId, Integer seq);

    void updateCommentPoint(Integer seq);

    void commentCancel(String userId, Integer seq);

    void cancelCommentPoint(Integer seq);

    Comment setDteTime(Comment comment);
}
