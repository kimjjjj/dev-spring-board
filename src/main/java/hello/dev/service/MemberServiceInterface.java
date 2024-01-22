package hello.dev.service;

import hello.dev.domain.Block;
import hello.dev.domain.Member;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public interface MemberServiceInterface {

    Member save(Member member);

    Map<String, String> checkError(Member member);

    Map<String, String> checkNick(String nickName);

    Integer findByUserPoint(String userId);

    void updateUserPoint(String userId, int seq);

    void cancelUserPoint(String userId, int seq);

    void scrapSave(String userId, int seq);

    void scrapCancel(String userId, int seq);

    Member addFavorite(String userId, String titleCode, Member member);

    Member removeFavorite(String userId, String titleCode, Member member);

    Member favoriteList(Member member, String userId);

    Map<Integer, String> favoriteMap (Member member);

    Member saveMypage(Member member, String userId, String nickName, String profileName, String profilePath);

    void setProfile(String uploadFileName, HttpServletRequest request) throws ServletException, IOException;

    void delete(String userId);

    void addBlock(Block block, String userId, String boardId);

    void cancelBlock(String userId, String blockId);
}
