package hello.dev.repository;

import hello.dev.domain.Block;
import hello.dev.domain.Member;

import java.sql.SQLException;

public interface MemberRepositoryInterface {

    Integer findByIdOrNick(String findColumn, String findValue);

    Member save(Member member) throws SQLException;

    Integer findByUserPoint(String userId);

    void updateUserPoint(String userId, int seq);

    void cancelUserPoint(String userId, int seq);

    void scrapSave(String userId, int seq);

    void scrapCancel(String userId, int seq);

    void addFavorite(String userId, String titleCode);

    void removeFavorite(String userId, String titleCode);

    Member favoriteList(Member member, String userId);

    void saveMypage(String userId, String nickName, String profileName, String profilePath);

    void deleteMember(String userId);

    void deleteFavorite(String userId);

    void addBlock(Block block, String userId, String boardId);

    void deleteBlock(String userId, String blockId);
}
