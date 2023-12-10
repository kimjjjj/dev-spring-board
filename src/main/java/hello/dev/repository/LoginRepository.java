package hello.dev.repository;

import hello.dev.connection.DBConnectionUtil;
import hello.dev.domain.Board;
import hello.dev.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class LoginRepository {

    private static final Map<Integer, Board> board = new HashMap<>();

    public Member login(String userId, String password) throws SQLException {
        log.info("<=====LoginRepository.login=====>");

        String sql =
                "WITH FAV AS ( " +
                    "SELECT ID, BOARD_CODE, ROWNUM AS ROW_NUMB " +
                    "FROM ( " +
                        "SELECT ID, BOARD_CODE " +
                        "FROM FAVORITE WHERE ID = ? " +
                        "ORDER BY INSDT)) " +
                "SELECT A.*, NVL(B.POINT, 0) + NVL(C.POINT, 0) AS USER_POINT " +
                ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 1) AS FAVORITE_1 " +
                ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 2) AS FAVORITE_2 " +
                ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 3) AS FAVORITE_3 " +
                ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 4) AS FAVORITE_4 " +
                ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 5) AS FAVORITE_5 " +
                ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 6) AS FAVORITE_6 " +
                ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 7) AS FAVORITE_7 " +
                ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 8) AS FAVORITE_8 " +
                ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 9) AS FAVORITE_9 " +
                ", (SELECT BOARD_CODE FROM FAV WHERE ROW_NUMB = 10) AS FAVORITE_10 " +
                "FROM MEMBER_INFORMATION A " +
                "LEFT JOIN ( " +
                    "SELECT SUM(POINT) AS POINT, INSID FROM BOARD GROUP BY INSID " +
                ") B ON A.ID = B.INSID " +
                "LEFT JOIN ( " +
                    "SELECT SUM(POINT) AS POINT, ID FROM BOARD_POINT GROUP BY ID " +
                ") C ON A.ID = C.ID " +
                "WHERE A.ID = ? AND A.PASSWORD = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);
            pstmt.setString(2, userId);
            pstmt.setString(3, password);

            rs = pstmt.executeQuery();

            Member member = new Member();

            if (rs.next()) {
                member.setUserId(userId);
                member.setPassword(password);
                member.setUserName(rs.getString("USER_NAME"));
                member.setBirth(rs.getString("BIRTH"));
                member.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                member.setNickName(rs.getString("NICKNAME"));
                member.setUserPoint(rs.getInt("USER_POINT"));
                member.setProfileName(rs.getString("PROFILE_NAME"));
                member.setProfilePath(rs.getString("PROFILE_PATH"));

                // 즐겨찾기
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

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}