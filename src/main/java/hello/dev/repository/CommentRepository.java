package hello.dev.repository;

import hello.dev.connection.DBConnectionUtil;
import hello.dev.domain.Board;
import hello.dev.domain.Comment;
import hello.dev.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepository {

    // 댓글 조회
    public List<Comment> findComment(String userId, Integer seq) throws SQLException {
        log.info("<=====CommentRepository.findComment=====>");

//        String sql = "SELECT *, ROWNUM AS ROW_NUMB FROM ( " +
        String sql = "SELECT A.SEQ, A.ID, A.CONTENT, A.TOP_SEQ, A.PARENT_SEQ, " +
                "A.LVL, A.ORDER_ROW, A.INSDT, B.NICKNAME, B.PROFILE_NAME, B.PROFILE_PATH, NVL(C.CNT, 0) AS CNT " +
                "FROM COMMENT A " +
                "LEFT JOIN MEMBER_INFORMATION B ON A.ID = B.ID " +
                "LEFT JOIN ( " +
                    "SELECT PARENT_SEQ, COUNT(*) AS CNT FROM LIKE_TB " +
                    "WHERE ID = ? AND LIKE_TYPE = 'comment' " +
                    "GROUP BY PARENT_SEQ) C ON A.SEQ = C.PARENT_SEQ " +
                "WHERE A.BOARD_SEQ = ? " +
                "ORDER BY NVL(A.TOP_SEQ, 0), NVL(A.PARENT_SEQ, 0), A.LVL, A.ORDER_ROW, A.INSDT ";
//                + ") ORDER BY ROW_NUMB";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);
            pstmt.setInt(2, seq);

            rs = pstmt.executeQuery();

            List<Comment> comments = new ArrayList<>();

            while (rs.next()) {
                Comment comment = new Comment();

                LocalDateTime nowDateTime = LocalDateTime.now();

                comment.setSeq(rs.getInt("SEQ"));
                comment.setUserId(rs.getString("ID"));
                comment.setContent(rs.getString("CONTENT"));
                comment.setTopSeq(rs.getInt("TOP_SEQ"));
                comment.setParentSeq(rs.getInt("PARENT_SEQ"));
                comment.setLvl(rs.getInt("LVL"));
                comment.setOrderRow(rs.getInt("ORDER_ROW"));
                comment.setNickName(rs.getString("NICKNAME"));
                comment.setProfileName(rs.getString("PROFILE_NAME"));
                comment.setProfilePath(rs.getString("PROFILE_PATH"));
                comment.setCnt(rs.getInt("CNT"));

                String[] dateArr = String.valueOf(rs.getDate("INSDT")).split("-");
                String[] timeArr = String.valueOf(rs.getTime("INSDT")).split(":");

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

                comments.add(comment);
            }

            return comments;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    // 댓글 등록
    public void saveParentComment(String userId, Integer boardSeq, String content
            , Integer lvl, Integer orderRow) throws SQLException {
        log.info("<=====CommentRepository.saveParentComment=====>");

        String sql = "INSERT INTO COMMENT (" +
                "ID, BOARD_SEQ, CONTENT, LVL, ORDER_ROW, INSDT) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            LocalDateTime nowDateTime = LocalDateTime.now();

            pstmt.setString(1, userId);
            pstmt.setInt(2, boardSeq);
            pstmt.setString(3, content);
            pstmt.setInt(4, lvl);
            pstmt.setInt(5, orderRow);
            pstmt.setString(6, String.valueOf(nowDateTime));

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("SEQ 조회 실패");
            }

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    // 대댓글 등록
    public void saveChildComment(String userId, Integer boardSeq, String content, Integer topSeq
            , Integer parentSeq, Integer lvl, Integer orderRow) throws SQLException {
        log.info("<=====CommentRepository.saveChildComment=====>");

        String sql = "INSERT INTO COMMENT (" +
                "ID, BOARD_SEQ, CONTENT, TOP_SEQ, PARENT_SEQ, LVL, ORDER_ROW, INSDT) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            LocalDateTime nowDateTime = LocalDateTime.now();

            pstmt.setString(1, userId);
            pstmt.setInt(2, boardSeq);
            pstmt.setString(3, content);
            pstmt.setInt(4, topSeq);
            pstmt.setInt(5, parentSeq);
            pstmt.setInt(6, lvl);
            pstmt.setInt(7, orderRow);
            pstmt.setString(8, String.valueOf(nowDateTime));

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("SEQ 조회 실패");
            }

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    // 상위 댓글의 ORDER_ROW보다 이후 ORDER_ROW가 있는지 체크
    public Integer chkComment(Integer topSeq, Integer orderRow) throws SQLException {
        log.info("<=====CommentRepository.chkComment=====>");

        String sql = "SELECT COUNT(*) AS CNT FROM COMMENT " +
                "WHERE (SEQ = ? OR TOP_SEQ = ?) AND ORDER_ROW > ? ";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, topSeq);
            pstmt.setInt(2, topSeq);
            pstmt.setInt(3, orderRow);

            rs = pstmt.executeQuery();

            int cnt = 0;
            if (rs.next()) {
                cnt = rs.getInt("CNT");
            }

            return cnt;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    // 댓글 ORDER_ROW 업데이트
    public void updateComment(Integer topSeq, Integer orderRow) throws SQLException {
        log.info("<=====CommentRepository.updateComment=====>");

        String sql = "UPDATE COMMENT SET ORDER_ROW = ORDER_ROW + 1 " +
                "WHERE SEQ IN ( " +
                "SELECT SEQ FROM COMMENT WHERE (SEQ = ? OR TOP_SEQ = ?) AND ORDER_ROW > ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, topSeq);
            pstmt.setInt(2, topSeq);
            pstmt.setInt(3, orderRow);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 좋아요 테이블 insert
    public void commentLike(String userId, Integer seq) throws SQLException {
        log.info("<=====CommentRepository.commentLike=====>");

        String sql = "INSERT INTO LIKE_TB (ID, PARENT_SEQ, LIKE_TYPE, INSDT) VALUES (?, ?, 'comment', ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);
            pstmt.setInt(2, seq);

            LocalDateTime nowDateTime = LocalDateTime.now();
            pstmt.setString(3, String.valueOf(nowDateTime));

            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 댓글 포인트 plus
    public void updateCommentPoint(int seq) throws SQLException {
        log.info("<=====CommentRepository.updateCommentPoint=====>");

        String sql = "UPDATE COMMENT SET POINT = " +
                "(SELECT POINT FROM COMMENT WHERE SEQ = ?) + 1 WHERE SEQ = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, seq);
            pstmt.setInt(2, seq);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 좋아요 테이블 delete
    public void commentCancel(String userId, int seq) throws SQLException {
        log.info("<=====CommentRepository.commentCancel=====>");

        String sql = "DELETE FROM LIKE_TB WHERE PARENT_SEQ = ? AND LIKE_TYPE = 'comment'" +
                "AND DECODE(ID, NULL, ID, NVL(?, '1')) = NVL(?, '1')";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, seq);
            pstmt.setString(2, userId);
            pstmt.setString(3, userId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 댓글 포인트 minus
    public void cancelCommentPoint(int seq) throws SQLException {
        log.info("<=====CommentRepository.cancelCommentPoint=====>");

        String sql = "UPDATE COMMENT SET POINT = " +
                "(SELECT POINT FROM COMMENT WHERE SEQ = ?) - 1 WHERE SEQ = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, seq);
            pstmt.setInt(2, seq);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}