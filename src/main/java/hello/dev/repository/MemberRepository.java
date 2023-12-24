package hello.dev.repository;

import hello.dev.connection.DBConnectionUtil;
import hello.dev.domain.Board;
import hello.dev.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class MemberRepository {

    private static final Map<Integer, Board> board = new HashMap<>();

    // ID와 닉네임이 존재하는지 체크
    public Integer findByIdOrNick(String findColumn, String findValue) throws SQLException {
        log.info("<=====MemberRepository.findByIdOrNick=====>");

        String sql = "";
        if ("ID".equals(findColumn)) {
            sql = "SELECT COUNT(*) AS CNT FROM MEMBER_INFORMATION WHERE ID = ?";
        } else if ("NICKNAME".equals(findColumn)) {
            sql = "SELECT COUNT(*) AS CNT FROM MEMBER_INFORMATION WHERE NICKNAME = ?";
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, findValue);

            rs = pstmt.executeQuery();

            int cnt = 0;

            while (rs.next()) {
                cnt = rs.getInt("CNT");
            }

            return cnt;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    // 회원가입
    public Member save(Member member) throws SQLException {
        log.info("<=====MemberRepository.save=====>");
        String sql = "INSERT INTO MEMBER_INFORMATION (" +
                "ID, PASSWORD, USER_NAME, BIRTH, PHONE_NUMBER, NICKNAME, TYPE, INSDT) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'normal', ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            LocalDateTime nowDateTime = LocalDateTime.now();

            pstmt.setString(1, member.getUserId());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getUserName());
            pstmt.setString(4, member.getBirth());
            pstmt.setString(5, member.getPhoneNumber());
            pstmt.setString(6, member.getNickName());
            pstmt.setString(7, String.valueOf(nowDateTime));

            pstmt.executeUpdate();

            return member;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 계정의 포인트 찾기
    public Integer findByUserPoint(String userId) throws SQLException {
        log.info("<=====MemberRepository.findByUserPoint=====>");

        String sql = "SELECT NVL(B.POINT, 0) + NVL(C.POINT, 0) + NVL(D.POINT, 0) AS USER_POINT " +
                "FROM MEMBER_INFORMATION A " +
                "LEFT JOIN (" +
                    "SELECT SUM(POINT) AS POINT, INSID FROM BOARD GROUP BY INSID " +
                ") B ON A.ID = B.INSID " +
                "LEFT JOIN (" +
                    "SELECT COUNT(*) AS POINT, ID FROM LIKE_TB GROUP BY ID " +
                ") C ON A.ID = C.ID " +
                "LEFT JOIN ( " +
                    "SELECT SUM(POINT) AS POINT, ID FROM COMMENT GROUP BY ID " +
                ") D ON A.ID = D.ID " +
                "WHERE A.ID = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            int point = 0;

            while (rs.next()) {
                point = rs.getInt("USER_POINT");
            }

            return point;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    // 유저 포인트 plus
    public String updateUserPoint(String userId, int seq) throws SQLException {
        log.info("<=====MemberRepository.updateUserPoint=====>");

        String sql = "INSERT INTO LIKE_TB (ID, PARENT_SEQ, LIKE_TYPE, INSDT) VALUES (?,?,'board',?)";

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

            return null;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 유저 포인트 minus
    public String cancelUserPoint(String userId, int seq) throws SQLException {
        log.info("<=====MemberRepository.cancelUserPoint=====>");

        String sql = "DELETE FROM LIKE_TB WHERE PARENT_SEQ = ? AND LIKE_TYPE = 'board'" +
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

            return null;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 게시판 즐겨찾기
    public String addFavorite(String userId, String titleCode) throws SQLException {
        log.info("<=====MemberRepository.addFavorite=====> userId : {}, titleCode : {}", userId, titleCode);

        String sql = "INSERT INTO FAVORITE (ID, BOARD_CODE, INSDT) VALUES (?, ?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            LocalDateTime nowDateTime = LocalDateTime.now();

            pstmt.setString(1, userId);
            pstmt.setString(2, titleCode);
            pstmt.setString(3, String.valueOf(nowDateTime));

            pstmt.executeUpdate();

            return null;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 게시판 즐겨찾기 해제
    public String removeFavorite(String userId, String titleCode) throws SQLException {
        log.info("<=====MemberRepository.removeFavorite=====> userId : {}, titleCode : {}", userId, titleCode);

        String sql = "DELETE FROM FAVORITE WHERE ID = ? AND BOARD_CODE = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);
            pstmt.setString(2, titleCode);

            pstmt.executeUpdate();

            return null;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 게시판 즐겨찾기 조회
    public Member favoriteList(Member member, String userId) throws SQLException {
        log.info("<=====MemberRepository.favoriteList=====>");

        String sql = "WITH FAV AS ( " +
                        "SELECT BOARD_CODE, ROWNUM AS ROW_NUMB " +
                        "FROM ( " +
                            "SELECT BOARD_CODE " +
                            "FROM FAVORITE " +
                            "WHERE ID = ? " +
                            "ORDER BY INSDT " +
                    ")) " +
                    "SELECT (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 1) AS FAVORITE_1 " +
                    ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 2) AS FAVORITE_2 " +
                    ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 3) AS FAVORITE_3 " +
                    ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 4) AS FAVORITE_4 " +
                    ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 5) AS FAVORITE_5 " +
                    ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 6) AS FAVORITE_6 " +
                    ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 7) AS FAVORITE_7 " +
                    ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 8) AS FAVORITE_8 " +
                    ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 9) AS FAVORITE_9 " +
                    ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 10) AS FAVORITE_10 " +
                    "FROM DUAL";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                member.setFavorite1(rs.getString("FAVORITE_1"));
                member.setFavorite2(rs.getString("FAVORITE_2"));
                member.setFavorite3(rs.getString("FAVORITE_3"));
                member.setFavorite4(rs.getString("FAVORITE_4"));
                member.setFavorite5(rs.getString("FAVORITE_5"));
                member.setFavorite6(rs.getString("FAVORITE_6"));
                member.setFavorite7(rs.getString("FAVORITE_7"));
                member.setFavorite8(rs.getString("FAVORITE_8"));
                member.setFavorite9(rs.getString("FAVORITE_9"));
                member.setFavorite10(rs.getString("FAVORITE_10"));
            }

            return member;

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        }
    }

    // 마이페이지 정보수정
    public void saveMypage(String userId, String nickName, String profileName, String profilePath) throws SQLException {
        log.info("<=====MemberRepository.saveMypage=====>");

        String sql = "UPDATE MEMBER_INFORMATION SET NICKNAME = ?, PROFILE_NAME = ?, PROFILE_PATH = ? WHERE ID = ?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, nickName);
            pstmt.setString(2, profileName);
            pstmt.setString(3, profilePath);
            pstmt.setString(4, userId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 회원탈퇴 - 계정 테이블 삭제
    public void deleteMember(String userId) throws SQLException {
        log.info("<=====MemberRepository.deleteMember=====>");

        String sql = "DELETE FROM MEMBER_INFORMATION WHERE ID = ?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("<=====db error=====>", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // 회원탈퇴 - 즐겨찾기 테이블 삭제
    public void deleteFavorite(String userId) throws SQLException {
        log.info("<=====MemberRepository.deleteFavorite=====>");

        String sql = "DELETE FROM FAVORITE WHERE ID = ?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);

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