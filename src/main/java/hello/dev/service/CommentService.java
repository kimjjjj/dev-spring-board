package hello.dev.service;

import hello.dev.domain.Comment;
import hello.dev.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService implements CommentServiceInterface {

    private final CommentRepository commentRepository;

    // 댓글 조회
    @Override
    public List<Comment> findComment(String userId, Integer seq) {
        log.info("<=====CommentService.findComment=====>");

        List<Comment> sqlComments = commentRepository.findComment(userId, seq);

        List<Comment> Parentcomments = new ArrayList<>(); // 상위 댓글
        List<Comment> childcomments = new ArrayList<>(); // 하위 댓글(대댓글)

        for (Comment comment : sqlComments) {
            // 현재시간부터 글 작성시간 까지 계산
            setDteTime(comment);

            if (comment.getTopSeq() == null || comment.getTopSeq() == 0) {
                Parentcomments.add(comment);
            } else {
                childcomments.add(comment);
            }
        }

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
    @Override
    public void saveParentComment(Comment comment, String userId, Integer boardSeq, String content
            , Integer lvl, Integer orderRow) {
        log.info("<=====CommentService.saveParentComment=====>");

        commentRepository.saveParentComment(comment, userId, boardSeq, content, lvl, orderRow);
    }

    // 대댓글 저장
    @Override
    public void saveChildComment(Comment comment, String userId, Integer boardSeq, String content, Integer topSeq
            , Integer parentSeq, Integer lvl, Integer orderRow) {
        log.info("<=====CommentService.saveChildComment=====>");

        if (topSeq == null || topSeq == 0) {
            topSeq = parentSeq;
        }

        commentRepository.saveChildComment(comment, userId, boardSeq, content, topSeq, parentSeq, lvl, orderRow);
    }

    // 댓글 수정
    @Override
    public void editComment(Integer seq, String content) {
        log.info("<=====CommentService.editComment=====>");

        commentRepository.editComment(seq, content);
    }

    // 댓글 삭제
    @Override
    public void deleteComment(Integer seq) {
        log.info("<=====CommentService.deleteComment=====>");

        commentRepository.deleteComment(seq);
    }

    // 게시글 삭제 시 댓글 삭제
    @Override
    public void deleteCommentBoard(Integer seq) {
        log.info("<=====CommentService.deleteCommentBoard=====>");

        commentRepository.deleteCommentBoard(seq);
    }

    // 상위 댓글의 ORDER_ROW보다 이후 ORDER_ROW가 있으면 +1
    @Override
    public void updateComment(Integer topSeq, Integer orderRow) {
        log.info("<=====CommentService.updateComment=====>");

        int cnt = commentRepository.chkComment(topSeq, orderRow);

        if (cnt > 0) {
            commentRepository.updateComment(topSeq, orderRow);
        }
    }

    // 좋아요 테이블 insert
    @Override
    public void commentLike(String userId, Integer seq) {
        log.info("<=====CommentService.commentLike=====>");

        commentRepository.commentLike(userId, seq);
    }

    // 댓글 포인트 plus
    @Override
    public void updateCommentPoint(Integer seq) {
        log.info("<=====CommentService.updateCommentPoint=====>");

        commentRepository.updateCommentPoint(seq);
    }

    // 좋아요 테이블 delete
    @Override
    public void commentCancel(String userId, Integer seq) {
        log.info("<=====CommentService.commentCancel=====>");

        commentRepository.commentCancel(userId, seq);
    }

    // 댓글 포인트 minus
    @Override
    public void cancelCommentPoint(Integer seq) {
        log.info("<=====CommentService.cancelCommentPoint=====>");

        commentRepository.cancelCommentPoint(seq);
    }

    // 현재시간부터 글 작성시간 까지 계산
    @Override
    public Comment setDteTime(Comment comment) {
        log.info("<=====CommentService.setDteTime=====>");

        // 날짜 계산
        LocalDateTime nowDateTime = LocalDateTime.now();

        String strDate = comment.getInsDt();
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
            comment.setInsDt(Integer.parseInt(dateArr[1]) + "." + Integer.parseInt(dateArr[2]));
        } else if (24 <= hour && hour <= 168) {
            comment.setInsDt(day + "일전");
        } else if (hour > 0) {
            comment.setInsDt(hour + "시간전");
        } else if (minute > 0) {
            comment.setInsDt(minute + "분전");
        } else {
            comment.setInsDt(second + "초전");
        }

        return comment;
    }
}
