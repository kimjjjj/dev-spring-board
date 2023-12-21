package hello.dev.repository;

import hello.dev.connection.DBConnectionUtil;
import hello.dev.domain.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private static final Map<Integer, Board> board = new HashMap<>();

    public List<Board> findChimList(Integer min, Integer max) throws SQLException {
        log.info("<=====BoardRepository.findChimList=====>");

        String sql = "WITH TEMP AS ( " +
                        "SELECT ROWNUM AS ROW_NUMB, A.* " +
                        "FROM ( " +
                            "SELECT * FROM BOARD " +
                            "WHERE POINT >= 10 " +
                            "ORDER BY SEQ DESC " +
                    ") A) " +
                    "SELECT * FROM TEMP " +
                    "WHERE ? <= ROW_NUMB AND ROW_NUMB <= ? " +
                    "ORDER BY ROW_NUMB";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, min);
            pstmt.setInt(2, max);

            rs = pstmt.executeQuery();

            LocalDateTime nowDateTime = LocalDateTime.now();

            List<Board> boards = new ArrayList<>();

            while (rs.next()) {
                Board board = new Board();
                board.setSeq(rs.getInt("seq"));
                board.setBoardNumber(rs.getString("BOARD_CODE"));
                board.setCategoryNumber(rs.getInt("CATEGORY_CODE"));
                board.setTxtName(rs.getString("txtName"));
                board.setComment(rs.getString("comment"));
                board.setTag(rs.getString("tag"));
                board.setInsId(rs.getString("insId"));
                board.setUptId(rs.getString("uptId"));
                board.setView(rs.getInt("view"));
                board.setPoint(rs.getInt("point"));

                // 날짜 계산
                String[] dateArr = String.valueOf(rs.getDate("insDt")).split("-");
                String[] timeArr = String.valueOf(rs.getTime("insDt")).split(":");

                LocalDateTime boardDateTime = LocalDateTime.of(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]),
                        Integer.parseInt(dateArr[2]), Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), Integer.parseInt(timeArr[2]));
                Duration duration = Duration.between(boardDateTime, nowDateTime);

                long day = duration.getSeconds()/(60*60)/24;
                long hour = duration.getSeconds()/(60*60);
                long minute = duration.getSeconds()/60;
                long second = duration.getSeconds();

                if (168 < hour) {
                    board.setInsDt(Integer.parseInt(dateArr[1]) + "." + Integer.parseInt(dateArr[2]));
                } else if (24 <= hour && hour <= 168) {
                    board.setInsDt(day + "일전");
                } else if (hour > 0) {
                    board.setInsDt(hour + "시간전");
                } else if (minute > 0) {
                    board.setInsDt(minute + "분전");
                } else {
                    board.setInsDt(second + "초전");
                }

                boards.add(board);
            }

            return boards;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    public List<Board> boardList(String titleCode, Integer min, Integer max) throws SQLException {
        log.info("<=====BoardRepository.boardList=====> titleCode: {}", titleCode);

        String sql = "WITH TEMP AS ( " +
                "SELECT ROWNUM AS ROW_NUMB, A.* " +
                "FROM ( " +
                "SELECT * FROM BOARD WHERE 1=1 ";

        if ("total_chim".equals(titleCode)) {
            sql += " AND BOARD_CODE IN ('notice', 'chim', 'chim_jjal', 'chim_fanart', 'request_stream', 'find_chimtube', 'make_short', 'favorite_chimtubu') ";
        } else if (!"all".equals(titleCode)) {
            sql += " AND BOARD_CODE = ?";
        }

        sql += "ORDER BY SEQ DESC " +
                ") A) " +
                "SELECT * FROM TEMP " +
                "WHERE ? <= ROW_NUMB AND ROW_NUMB <= ? " +
                "ORDER BY ROW_NUMB";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            if (!"all".equals(titleCode) && !"total_chim".equals(titleCode)) {
                pstmt.setString(1, titleCode);
                pstmt.setInt(2, min);
                pstmt.setInt(3, max);
            } else {
                pstmt.setInt(1, min);
                pstmt.setInt(2, max);
            }

            rs = pstmt.executeQuery();

            LocalDateTime nowDateTime = LocalDateTime.now();

            List<Board> boards = new ArrayList<>();

            while (rs.next()) {
                Board board = new Board();
                board.setSeq(rs.getInt("seq"));
                board.setBoardNumber(rs.getString("BOARD_CODE"));
                board.setCategoryNumber(rs.getInt("CATEGORY_CODE"));
                board.setTxtName(rs.getString("txtName"));
                board.setComment(rs.getString("comment"));
                board.setTag(rs.getString("tag"));
                board.setInsId(rs.getString("insId"));
                board.setUptId(rs.getString("uptId"));
                board.setView(rs.getInt("view"));
                board.setPoint(rs.getInt("point"));

                // 날짜 계산
                String[] dateArr = String.valueOf(rs.getDate("insDt")).split("-");
                String[] timeArr = String.valueOf(rs.getTime("insDt")).split(":");

                LocalDateTime boardDateTime = LocalDateTime.of(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]),
                        Integer.parseInt(dateArr[2]), Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), Integer.parseInt(timeArr[2]));
                Duration duration = Duration.between(boardDateTime, nowDateTime);

                long day = duration.getSeconds()/(60*60)/24;
                long hour = duration.getSeconds()/(60*60);
                long minute = duration.getSeconds()/60;
                long second = duration.getSeconds();

                if (168 < hour) {
                    board.setInsDt(Integer.parseInt(dateArr[1]) + "." + Integer.parseInt(dateArr[2]));
                } else if (24 <= hour && hour <= 168) {
                    board.setInsDt(day + "일전");
                } else if (hour > 0) {
                    board.setInsDt(hour + "시간전");
                } else if (minute > 0) {
                    board.setInsDt(minute + "분전");
                } else {
                    board.setInsDt(second + "초전");
                }

                boards.add(board);
            }

            return boards;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    public Board findPost(String userId, int seq, String titleCode) throws SQLException {
        log.info("<=====BoardRepository.findPost=====>{} {} {}", userId, seq, titleCode);
//        String sql = "SELECT A.*, COUNT(*) AS CNT FROM BOARD A " +
//                "LEFT JOIN LIKE_TB B " +
//                "ON A.SEQ = B.PARENT_SEQ WHERE A.SEQ = ? AND B.LIKE_TYPE = 'board'" +
//                "AND DECODE(B.ID, NULL, B.ID, NVL(?, 1)) = NVL(?, 1)";
        String sql = "SELECT A.*, NVL(B.CNT, 0) AS CNT " +
                    "FROM ( " +
                        "SELECT * " +
                        ", LAG(SEQ) OVER(ORDER BY SEQ DESC) AS BEFORE_SEQ " +
                        ", LEAD(SEQ) OVER(ORDER BY SEQ DESC) AS AFTER_SEQ " +
                        "FROM BOARD WHERE 1=1 ";

        if ("chimhaha".equals(titleCode)) {
            sql += " AND POINT >= 10 ";
        } else if ("total_chim".equals(titleCode)) {
            sql += " AND BOARD_CODE IN ('notice', 'chim', 'chim_jjal', 'chim_fanart', 'request_stream', 'find_chimtube', 'make_short', 'favorite_chimtubu') ";
        } else if (!"all".equals(titleCode)) {
            sql += " AND BOARD_CODE = ? ";
        }
        sql += "ORDER BY SEQ DESC) A " +
                    "LEFT JOIN ( " +
                        "SELECT PARENT_SEQ, COUNT(*) AS CNT FROM LIKE_TB " +
                        "WHERE ID = NVL(?, 0) AND LIKE_TYPE = 'board' " +
                        "GROUP BY PARENT_SEQ) B " +
                    "ON A.SEQ = B.PARENT_SEQ WHERE A.SEQ = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

//            pstmt.setInt(1, seq);
//            pstmt.setString(2, userId);
//            pstmt.setString(3, userId);

            if (!"all".equals(titleCode) && !"chimhaha".equals(titleCode) && !"total_chim".equals(titleCode)) {
                pstmt.setString(1, titleCode);
                pstmt.setString(2, userId);
                pstmt.setInt(3, seq);
            } else {
                pstmt.setString(1, userId);
                pstmt.setInt(2, seq);
            }

            rs = pstmt.executeQuery();

            Board board = new Board();

            if (rs.next()) {
                LocalDateTime nowDateTime = LocalDateTime.now();

                board.setSeq(seq);
                board.setBeforeSeq(rs.getInt("BEFORE_SEQ"));
                board.setAfterSeq(rs.getInt("AFTER_SEQ"));
                board.setBoardNumber(rs.getString("BOARD_CODE"));
                board.setCategoryNumber(rs.getInt("CATEGORY_CODE"));
                board.setTxtName(rs.getString("txtName"));
                board.setComment(rs.getString("comment"));
                board.setTag(rs.getString("tag"));
                board.setInsId(rs.getString("insId"));
                board.setUptId(rs.getString("uptId"));
                board.setView(rs.getInt("view"));
                board.setPoint(rs.getInt("point"));
                board.setCnt(rs.getInt("cnt"));
                String[] dateArr = String.valueOf(rs.getDate("insDt")).split("-");
                String[] timeArr = String.valueOf(rs.getTime("insDt")).split(":");

                LocalDateTime boardDateTime = LocalDateTime.of(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]),
                        Integer.parseInt(dateArr[2]), Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), Integer.parseInt(timeArr[2]));
                Duration duration = Duration.between(boardDateTime, nowDateTime);

                long day = duration.getSeconds()/(60*60)/24;
                long hour = duration.getSeconds()/(60*60);
                long minute = duration.getSeconds()/60;
                long second = duration.getSeconds();

                if (168 < hour) {
                    board.setInsDt(Integer.parseInt(dateArr[1]) + "." + Integer.parseInt(dateArr[2]));
                } else if (24 <= hour && hour <= 168) {
                    board.setInsDt(day + "일전");
                } else if (hour > 0) {
                    board.setInsDt(hour + "시간전");
                } else if (minute > 0) {
                    board.setInsDt(minute + "분전");
                } else {
                    board.setInsDt(second + "초전");
                }
            }

            return board;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    public String updateView(int seq) throws SQLException {
        log.info("<=====BoardRepository.updateView=====>");
        String sql = "UPDATE BOARD SET VIEW = " +
                "(SELECT VIEW FROM BOARD WHERE SEQ = ?) + 1 WHERE SEQ = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, seq);
            pstmt.setInt(2, seq);
            pstmt.executeUpdate();

            return null;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Board save(Board board) throws SQLException {
        log.info("<=====BoardRepository.save=====>");
        String sql = "INSERT INTO BOARD (" +
                "BOARD_CODE, CATEGORY_CODE, TXTNAME, COMMENT, TAG, INSID, INSDT) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            LocalDateTime nowDateTime = LocalDateTime.now();

            pstmt.setString(1, board.getBoardCode());
            pstmt.setInt(2, Integer.parseInt(board.getCategoryCode()));
            pstmt.setString(3, board.getTxtName());
            pstmt.setString(4, board.getComment());
            pstmt.setString(5, board.getTag());
            pstmt.setString(6, board.getInsId());
            pstmt.setString(7, String.valueOf(nowDateTime));

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                board.setSeq(rs.getInt(1));
            } else {
                throw new SQLException("SEQ 조회 실패");
            }

            return board;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    public Board saveImg(Board board) throws SQLException {
        log.info("<=====BoardRepository.saveImg=====>");
        String sql = "INSERT INTO ATTACH (PARENT_SEQ, FILENAME, SAVE_FILENAME, PATH, INSID, INSDT)" +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            LocalDateTime nowDateTime = LocalDateTime.now();

            pstmt.setInt(1, board.getSeq());
            pstmt.setString(2, "");
            pstmt.setString(3, board.getSaveFileName());
            pstmt.setString(4, board.getPath());
            pstmt.setString(5, board.getInsId());
            pstmt.setString(6, String.valueOf(nowDateTime));

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("SEQ 조회 실패");
            }

            return board;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    // 게시글 수정
    public void updatePost(Board board, String userId) throws SQLException {
        log.info("<=====BoardRepository.updatePost=====>");
        String sql = "UPDATE BOARD SET BOARD_CODE = ?, CATEGORY_CODE = ? " +
                    ", TXTNAME = ?, COMMENT = ?, TAG = ?, UPTID = ?, UPTDT = ? " +
                    "WHERE SEQ = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, board.getBoardCode());
            pstmt.setString(2, board.getCategoryCode());
            pstmt.setString(3, board.getTxtName());
            pstmt.setString(4, board.getComment());
            pstmt.setString(5, board.getTag());
            pstmt.setString(6, userId);

            LocalDateTime nowDateTime = LocalDateTime.now();
            pstmt.setString(7, String.valueOf(nowDateTime));

            pstmt.setInt(8, board.getSeq());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 게시글 포인트 plus
    public String updateLike(int seq) throws SQLException {
        log.info("<=====BoardRepository.updateLike=====>");
        String sql = "UPDATE BOARD SET POINT = " +
                "(SELECT POINT FROM BOARD WHERE SEQ = ?) + 1 WHERE SEQ = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, seq);
            pstmt.setInt(2, seq);
            pstmt.executeUpdate();

            return null;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 게시글 포인트 minus
    public String cancelLike(int seq) throws SQLException {
        log.info("<=====BoardRepository.cancelLike=====>");
        String sql = "UPDATE BOARD SET POINT = " +
                "(SELECT POINT FROM BOARD WHERE SEQ = ?) - 1 WHERE SEQ = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, seq);
            pstmt.setInt(2, seq);
            pstmt.executeUpdate();

            return null;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }
    
    // 검색
    public List<Board> search(String searchKeyword, String searchType) throws SQLException {
        log.info("<=====BoardRepository.search=====> searchKeyword : {}, searchType : {}", searchKeyword, searchType);

        String sql = "";
        if ("title".equals(searchType)) {
            sql = "SELECT * FROM BOARD WHERE TXTNAME LIKE '%' || ? || '%' ORDER BY SEQ DESC";
        } else if ("titleAndContent".equals(searchType)) {
            sql = "SELECT * FROM BOARD WHERE TXTNAME LIKE '%' || ? || '%' OR COMMENT LIKE '%' || ? || '%' ORDER BY SEQ DESC";
        } else if ("nickname".equals(searchType)) {
            sql = "SELECT * FROM BOARD WHERE INSID = (SELECT ID FROM MEMBER_INFORMATION WHERE NICKNAME = ?) ORDER BY SEQ DESC";
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, searchKeyword);

            if ("titleAndContent".equals(searchType)) {
                pstmt.setString(2, searchKeyword);
            }

            rs = pstmt.executeQuery();

            List<Board> boards = new ArrayList<>();

            while (rs.next()) {
                Board board = new Board();

                LocalDateTime nowDateTime = LocalDateTime.now();

                board.setSeq(rs.getInt("SEQ"));
                board.setBoardNumber(rs.getString("BOARD_CODE"));
                board.setCategoryNumber(rs.getInt("CATEGORY_CODE"));
                board.setTxtName(rs.getString("txtName"));
                board.setComment(rs.getString("comment"));
                board.setTag(rs.getString("tag"));
                board.setInsId(rs.getString("insId"));
                board.setUptId(rs.getString("uptId"));
                board.setView(rs.getInt("view"));
                board.setPoint(rs.getInt("point"));
                String[] dateArr = String.valueOf(rs.getDate("insDt")).split("-");
                String[] timeArr = String.valueOf(rs.getTime("insDt")).split(":");

                LocalDateTime boardDateTime = LocalDateTime.of(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]),
                        Integer.parseInt(dateArr[2]), Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), Integer.parseInt(timeArr[2]));
                Duration duration = Duration.between(boardDateTime, nowDateTime);

                long day = duration.getSeconds()/(60*60)/24;
                long hour = duration.getSeconds()/(60*60);
                long minute = duration.getSeconds()/60;
                long second = duration.getSeconds();

                if (168 < hour) {
                    board.setInsDt(Integer.parseInt(dateArr[1]) + "." + Integer.parseInt(dateArr[2]));
                } else if (24 <= hour && hour <= 168) {
                    board.setInsDt(day + "일전");
                } else if (hour > 0) {
                    board.setInsDt(hour + "시간전");
                } else if (minute > 0) {
                    board.setInsDt(minute + "분전");
                } else {
                    board.setInsDt(second + "초전");
                }

                boards.add(board);
            }

            return boards;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    // 마이페이지 내가 쓴 글 조회
    public List<Board> mypagePost(String userId) throws SQLException {
        log.info("<=====BoardRepository.mypagePost=====>");

        String sql = "SELECT * FROM BOARD WHERE INSID = ? ORDER BY INSDT";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            List<Board> boards = new ArrayList<>();

            while (rs.next()) {
                Board board = new Board();

                board.setSeq(rs.getInt("SEQ"));
                board.setTxtName(rs.getString("TXTNAME"));
                board.setInsDt(rs.getString("INSDT"));

                boards.add(board);
            }

            return boards;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    // 마이페이지 내가 쓴 댓글 조회
    public List<Board> mypageComment(String userId) throws SQLException {
        log.info("<=====BoardRepository.mypageComment=====>");

        String sql = "SELECT BOARD_SEQ AS SEQ, SEQ AS COMMENT_SEQ, CONTENT, INSDT FROM COMMENT WHERE ID = ? ORDER BY INSDT";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            List<Board> boards = new ArrayList<>();

            while (rs.next()) {
                Board board = new Board();

                board.setSeq(rs.getInt("SEQ"));
                board.setCommentSeq(rs.getInt("COMMENT_SEQ"));
                board.setTxtName(rs.getString("CONTENT"));
                board.setInsDt(rs.getString("INSDT"));

                boards.add(board);
            }

            return boards;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    // 마이페이지 좋아요 한 글 조회
    public List<Board> mypageLikePost(String userId) throws SQLException {
        log.info("<=====BoardRepository.mypageLikePost=====>");

        String sql = "SELECT A.PARENT_SEQ AS SEQ, B.TXTNAME, B.INSID, B.INSDT " +
                "FROM LIKE_TB A " +
                "LEFT JOIN BOARD B " +
                "ON A.PARENT_SEQ = B.SEQ " +
                "WHERE A.ID = ? AND A.LIKE_TYPE = 'board'" +
                "ORDER BY B.INSDT";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            List<Board> boards = new ArrayList<>();

            while (rs.next()) {
                Board board = new Board();

                board.setSeq(rs.getInt("SEQ"));
                board.setTxtName(rs.getString("TXTNAME"));
                board.setInsId(rs.getString("INSID"));
                board.setInsDt(rs.getString("INSDT"));

                boards.add(board);
            }

            return boards;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    // 마이페이지 좋아요 한 댓글 조회
    public List<Board> mypageLikeComment(String userId) throws SQLException {
        log.info("<=====BoardRepository.mypageLikeComment=====>");

        String sql = "SELECT B.BOARD_SEQ AS SEQ, A.PARENT_SEQ AS COMMENT_SEQ, B.CONTENT, B.ID, B.INSDT " +
                    "FROM LIKE_TB A " +
                    "LEFT JOIN COMMENT B ON A.PARENT_SEQ = B.SEQ " +
                    "WHERE A.ID = ? AND A.LIKE_TYPE = 'comment' " +
                    "ORDER BY B.INSDT";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            List<Board> boards = new ArrayList<>();

            while (rs.next()) {
                Board board = new Board();

                board.setSeq(rs.getInt("SEQ"));
                board.setCommentSeq(rs.getInt("COMMENT_SEQ"));
                board.setTxtName(rs.getString("CONTENT"));
                board.setInsId(rs.getString("ID"));
                board.setInsDt(rs.getString("INSDT"));

                boards.add(board);
            }

            return boards;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
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