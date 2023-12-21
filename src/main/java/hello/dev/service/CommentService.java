package hello.dev.service;

import hello.dev.domain.Board;
import hello.dev.domain.Comment;
import hello.dev.domain.Member;
import hello.dev.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // 댓글 조회
    public List<Comment> findComment(String userId, Integer seq) throws SQLException {
        log.info("<=====CommentService.findComment=====>");

        List<Comment> sqlComments = commentRepository.findComment(userId, seq);

        List<Comment> Parentcomments = new ArrayList<>(); // 상위 댓글
        List<Comment> childcomments = new ArrayList<>(); // 하위 댓글(대댓글)

        for (Comment comment : sqlComments) {
            if (comment.getTopSeq() == null || comment.getTopSeq() == 0) {
                Parentcomments.add(comment);
            } else {
                childcomments.add(comment);
            }
        }

//        for (Comment comment : Parentcomments) {
//            log.info("{},{},{},{},{},{}", comment.getSeq(), comment.getContent(), comment.getTopSeq(), comment.getParentSeq(), comment.getLvl(), comment.getOrderRow());
//        }
//
//        log.info("");
//
//        for (Comment comment : childcomments) {
//            log.info("{},{},{},{},{},{}", comment.getSeq(), comment.getContent(), comment.getTopSeq(), comment.getParentSeq(), comment.getLvl(), comment.getOrderRow());
//        }

        List<Comment> comments = new ArrayList<>();
        int cnt = 1; // 하위댓글(대댓글)은 들여쓰기가 되도록 위에서부터 카운트
        int len = childcomments.size();

        for (Comment parentComment : Parentcomments) {
            int order = 2; // 상위에서 하위댓글로 같은 그룹은 순서대로

            parentComment.setRownumb(cnt);
            cnt++;

            comments.add(parentComment);

            for (int i=0; i<len; i++) {
                for (Comment childComment : childcomments) {
                    if (parentComment.getSeq() == childComment.getTopSeq()
                            && order == childComment.getOrderRow()) {

                        childComment.setRownumb(cnt);
                        cnt++;

                        comments.add(childComment);
                        order++;

                        break;
                    }
                }
            }
        }

        return comments;
    }

    // 댓글 저장
    public void saveParentComment(String userId, Integer boardSeq, String content
            , Integer lvl, Integer orderRow) throws SQLException {
        log.info("<=====CommentService.saveParentComment=====>");

        commentRepository.saveParentComment(userId, boardSeq, content, lvl, orderRow);
    }

    // 대댓글 저장
    public void saveChildComment(String userId, Integer boardSeq, String content, Integer topSeq
            , Integer parentSeq, Integer lvl, Integer orderRow) throws SQLException {
        log.info("<=====CommentService.saveChildComment=====>");

        if (topSeq == 0) {
            topSeq = parentSeq;
        }

        commentRepository.saveChildComment(userId, boardSeq, content, topSeq, parentSeq, lvl, orderRow);
    }

    // 상위 댓글의 ORDER_ROW보다 이후 ORDER_ROW가 있으면 +1
    public void updateComment(Integer topSeq, Integer orderRow) throws SQLException {
        log.info("<=====CommentService.updateComment=====>");

        int cnt = commentRepository.chkComment(topSeq, orderRow);

        if (cnt > 0) {
            commentRepository.updateComment(topSeq, orderRow);
        }
    }

    // 좋아요 테이블 insert
    public void commentLike(String userId, Integer seq) throws SQLException {
        log.info("<=====CommentService.commentLike=====>");

        commentRepository.commentLike(userId, seq);
    }

    // 댓글 포인트 plus
    public void updateCommentPoint(Integer seq) throws SQLException {
        log.info("<=====CommentService.updateCommentPoint=====>");

        commentRepository.updateCommentPoint(seq);
    }

    // 좋아요 테이블 delete
    public void commentCancel(String userId, Integer seq) throws SQLException {
        log.info("<=====CommentService.commentCancel=====>");

        commentRepository.commentCancel(userId, seq);
    }

    // 댓글 포인트 minus
    public void cancelCommentPoint(Integer seq) throws SQLException {
        log.info("<=====CommentService.cancelCommentPoint=====>");

        commentRepository.cancelCommentPoint(seq);
    }
}
