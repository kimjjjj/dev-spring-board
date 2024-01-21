package hello.dev.mybatis;

import hello.dev.domain.Block;
import hello.dev.domain.Board;
import hello.dev.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    Integer findByIdOrNick(@Param("findColumn") String findColumn, @Param("findValue") String findValue);

    void save(Member member);

    Integer findByUserPoint(String userId);

    void updateUserPoint(@Param("userId") String userId, @Param("seq") int seq);

    void cancelUserPoint(@Param("userId") String userId, @Param("seq") int seq);

    void scrapSave(@Param("userId") String userId, @Param("seq") int seq);

    void scrapCancel(@Param("userId") String userId, @Param("seq") int seq);

    void addFavorite(@Param("userId") String userId, @Param("titleCode") String titleCode);

    void removeFavorite(@Param("userId") String userId, @Param("titleCode") String titleCode);

    Member favoriteList(@Param("member") Member member, @Param("userId") String userId);

    void saveMypage(@Param("userId") String userId, @Param("nickName") String nickName
            , @Param("profileName") String profileName, @Param("profilePath") String profilePath);

    void deleteMember(String userId);

    void deleteFavorite(String userId);

    void addBlock(@Param("block") Block block, @Param("userId") String userId, @Param("boardId") String boardId);

    void deleteBlock(@Param("userId") String userId, @Param("blockId") String blockId);
}
