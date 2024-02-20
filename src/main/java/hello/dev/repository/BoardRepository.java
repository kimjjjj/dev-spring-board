package hello.dev.repository;

import hello.dev.domain.Board;
import hello.dev.mybatis.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final BoardMapper boardMapper;
    private static final Map<Integer, Board> board = new HashMap<>();

    public List<Board> findChimList(Integer min, Integer max, String userId) {
        log.info("<=====BoardRepository.findChimList=====>{}, {}, {}", min, max, userId);

        return boardMapper.findChimList(min, max, userId);
    }

    public List<Board> boardList(String titleCode, Integer min, Integer max, String userId) {
        log.info("<=====BoardRepository.boardList=====> titleCode: {}, {}, {}, {}", titleCode, min, max, userId);

        return boardMapper.boardList(titleCode, min, max, userId);
    }

    public Board findPost(String userId, int seq, String titleCode) {
        log.info("<=====BoardRepository.findPost=====>{} {} {}", userId, seq, titleCode);

        return boardMapper.findPost(userId, seq, titleCode);
    }

    public void updateView(int seq) {
        log.info("<=====BoardRepository.updateView=====>");

        boardMapper.updateView(seq);
    }

    public Board save(Board board) {
        log.info("<=====BoardRepository.save=====>");

        boardMapper.save(board);

        return board;
    }

    public Board saveImg(Board board) {
        log.info("<=====BoardRepository.saveImg=====>");

        boardMapper.saveImg(board);

        return board;
    }

    // 게시글 수정
    public void updatePost(Board board, String userId) {
        log.info("<=====BoardRepository.updatePost=====>");

        boardMapper.updatePost(board, userId);
    }

    // 게시글 삭제
    public void deletePost(Integer seq) {
        log.info("<=====BoardRepository.deletePost=====>");

        boardMapper.deletePost(seq);
    }

    // 첨부파일 삭제
    public void deleteImg(Integer seq) {
        log.info("<=====BoardRepository.deleteImg=====>");

        boardMapper.deleteImg(seq);
    }

    // 게시글 삭제 시 좋아요 삭제
    public void deleteLike(Integer seq) {
        log.info("<=====BoardRepository.deleteLike=====>");

        boardMapper.deleteLike(seq);
    }

    // 게시글 포인트 plus
    public void updateLike(int seq) {
        log.info("<=====BoardRepository.updateLike=====>");

        boardMapper.updateLike(seq);
    }

    // 게시글 포인트 minus
    public void cancelLike(int seq) {
        log.info("<=====BoardRepository.cancelLike=====>");

        boardMapper.cancelLike(seq);
    }

    // 검색
    public List<Board> search(String searchKeyword, String searchType, Integer min, Integer max, String userId) {
        log.info("<=====BoardRepository.search=====> searchKeyword : {}, searchType : {}", searchKeyword, searchType);

        return boardMapper.search(searchKeyword, searchType, min, max, userId);
    }

    // 마이페이지 내가 쓴 글 조회
    public List<Board> mypagePost(String userId) {
        log.info("<=====BoardRepository.mypagePost=====>");

        return boardMapper.mypagePost(userId);
    }

    // 마이페이지 내가 쓴 댓글 조회
    public List<Board> mypageComment(String userId) {
        log.info("<=====BoardRepository.mypageComment=====>");

        return boardMapper.mypageComment(userId);
    }

    // 마이페이지 좋아요 한 글 조회
    public List<Board> mypageLikePost(String userId) {
        log.info("<=====BoardRepository.mypageLikePost=====>");

        return boardMapper.mypageLikePost(userId);
    }

    // 마이페이지 좋아요 한 댓글 조회
    public List<Board> mypageLikeComment(String userId) {
        log.info("<=====BoardRepository.mypageLikeComment=====>");

        return boardMapper.mypageLikeComment(userId);
    }

    // 마이페이지 스크랩 한 글 조회
    public List<Board> mypageScrap(String userId) {
        log.info("<=====BoardRepository.mypageScrap=====>");

        return boardMapper.mypageScrap(userId);
    }

    // 마이페이지 차단한 사용자 조회
    public List<Board> mypageBlock(String userId) {
        log.info("<=====BoardRepository.mypageBlock=====>");

        return boardMapper.mypageBlock(userId);
    }

    // 유저 게시글 조회
    public List<Board> userPagePost(String nickName) {
        log.info("<=====BoardRepository.userPagePost=====>");

        return boardMapper.userPagePost(nickName);
    }

    // 유저 댓글 조회
    public List<Board> userPageComment(String nickName) {
        log.info("<=====BoardRepository.userPageComment=====>");

        return boardMapper.userPageComment(nickName);
    }

    // 회원탈퇴 - 게시글 테이블 삭제
    public void deleteBoardById(String userId) {
        log.info("<=====BoardRepository.deleteBoardById=====>");

        boardMapper.deleteBoardById(userId);
    }

    // 회원탈퇴 - 첨부파일 테이블 삭제
    public void deleteAttachById(String userId) {
        log.info("<=====BoardRepository.deleteAttachById=====>");

        boardMapper.deleteAttachById(userId);
    }

    // 회원탈퇴 - 좋아요 테이블 삭제
    public void deleteLikeById(String userId) {
        log.info("<=====BoardRepository.deleteLikeById=====>");

        boardMapper.deleteLikeById(userId);
    }
}