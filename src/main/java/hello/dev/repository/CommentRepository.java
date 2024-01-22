package hello.dev.repository;

import hello.dev.domain.Comment;
import hello.dev.mybatis.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepository implements CommentRepositoryInterface {

    private final CommentMapper commentMapper;

    // 댓글 조회
    @Override
    public List<Comment> findComment(String userId, Integer seq) {
        log.info("<=====CommentRepository.findComment=====>");

        return commentMapper.findComment(userId, seq);
    }

    // 댓글 등록
    @Override
    public void saveParentComment(Comment comment, String userId, Integer boardSeq, String content
            , Integer lvl, Integer orderRow) {
        log.info("<=====CommentRepository.saveParentComment=====>");

        commentMapper.saveParentComment(comment, userId, boardSeq, content, lvl, orderRow);
    }

    // 대댓글 등록
    @Override
    public void saveChildComment(Comment comment, String userId, Integer boardSeq, String content, Integer topSeq
            , Integer parentSeq, Integer lvl, Integer orderRow) {
        log.info("<=====CommentRepository.saveChildComment=====>");

        commentMapper.saveChildComment(comment, userId, boardSeq, content, topSeq, parentSeq, lvl, orderRow);
    }

    // 댓글 수정
    @Override
    public void editComment(Integer seq, String content) {
        log.info("<=====CommentRepository.editComment=====>");

        commentMapper.editComment(seq, content);
    }

    // 댓글 삭제
    @Override
    public void deleteComment(Integer seq) {
        log.info("<=====CommentRepository.deleteComment=====>");

        commentMapper.deleteComment(seq);
    }

    // 게시글 삭제 시 댓글 삭제
    @Override
    public void deleteCommentBoard(Integer seq) {
        log.info("<=====CommentRepository.deleteCommentBoard=====>");

        commentMapper.deleteCommentBoard(seq);
    }

    // 상위 댓글의 ORDER_ROW보다 이후 ORDER_ROW가 있는지 체크
    @Override
    public Integer chkComment(Integer topSeq, Integer orderRow) {
        log.info("<=====CommentRepository.chkComment=====>");

        return commentMapper.chkComment(topSeq, orderRow);
    }

    // 댓글 ORDER_ROW 업데이트
    @Override
    public void updateComment(Integer topSeq, Integer orderRow) {
        log.info("<=====CommentRepository.updateComment=====>");

        commentMapper.updateComment(topSeq, orderRow);
    }

    // 좋아요 테이블 insert
    @Override
    public void commentLike(String userId, Integer seq) {
        log.info("<=====CommentRepository.commentLike=====>");

        commentMapper.commentLike(userId, seq);
    }

    // 댓글 포인트 plus
    @Override
    public void updateCommentPoint(int seq) {
        log.info("<=====CommentRepository.updateCommentPoint=====>");

        commentMapper.updateCommentPoint(seq);
    }

    // 좋아요 테이블 delete
    @Override
    public void commentCancel(String userId, int seq) {
        log.info("<=====CommentRepository.commentCancel=====>");

        commentMapper.commentCancel(userId, seq);
    }

    // 댓글 포인트 minus
    @Override
    public void cancelCommentPoint(int seq) {
        log.info("<=====CommentRepository.cancelCommentPoint=====>");

        commentMapper.cancelCommentPoint(seq);
    }

    // 회원탈퇴 - 댓글 테이블 delete
    @Override
    public void deleteCommentById(String userId) {
        log.info("<=====CommentRepository.deleteCommentById=====>");

        commentMapper.deleteCommentById(userId);
    }
}