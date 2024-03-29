package hello.dev.repository;

import hello.dev.domain.Block;
import hello.dev.domain.Board;
import hello.dev.domain.Member;
import hello.dev.mybatis.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final MemberMapper memberMapper;
    private static final Map<Integer, Board> board = new HashMap<>();

    // ID와 닉네임이 존재하는지 체크
    public Integer findByIdOrNick(String findColumn, String findValue) {
        log.info("<=====MemberRepository.findByIdOrNick=====>");

        return memberMapper.findByIdOrNick(findColumn, findValue);
    }

    // 회원가입
    public Member save(Member member) {
        log.info("<=====MemberRepository.save=====>");

        memberMapper.save(member);

        return member;
    }

    // 계정의 포인트 찾기
    public Integer findByUserPoint(String userId) {
        log.info("<=====MemberRepository.findByUserPoint=====>");

        return memberMapper.findByUserPoint(userId);
    }

    // 유저 포인트 plus
    public void updateUserPoint(String userId, int seq) {
        log.info("<=====MemberRepository.updateUserPoint=====>");

        memberMapper.updateUserPoint(userId, seq);
    }

    // 유저 포인트 minus
    public void cancelUserPoint(String userId, int seq) {
        log.info("<=====MemberRepository.cancelUserPoint=====>");

        memberMapper.cancelUserPoint(userId, seq);
    }

    // 스크랩 저장
    public void scrapSave(String userId, int seq) {
        log.info("<=====MemberRepository.scrapSave=====>");

        memberMapper.scrapSave(userId, seq);
    }

    // 스크랩 취소
    public void scrapCancel(String userId, int seq) {
        log.info("<=====MemberRepository.scrapCancel=====>");

        memberMapper.scrapCancel(userId, seq);
    }

    // 게시판 즐겨찾기
    public void addFavorite(String userId, String titleCode) {
        log.info("<=====MemberRepository.addFavorite=====> userId : {}, titleCode : {}", userId, titleCode);

        memberMapper.addFavorite(userId, titleCode);
    }

    // 게시판 즐겨찾기 해제
    public void removeFavorite(String userId, String titleCode) {
        log.info("<=====MemberRepository.removeFavorite=====> userId : {}, titleCode : {}", userId, titleCode);

        memberMapper.removeFavorite(userId, titleCode);
    }

    // 게시판 즐겨찾기 조회
    public Member favoriteList(Member member, String userId) {
        log.info("<=====MemberRepository.favoriteList=====>");

        return memberMapper.favoriteList(member, userId);
    }

    // 마이페이지 정보수정
    public void saveMypage(String userId, String nickName, String profileName, String profilePath) {
        log.info("<=====MemberRepository.saveMypage=====>");

        memberMapper.saveMypage(userId, nickName, profileName, profilePath);
    }

    // 회원탈퇴 - 계정 테이블 삭제
    public void deleteMember(String userId) {
        log.info("<=====MemberRepository.deleteMember=====>");

        memberMapper.deleteMember(userId);
    }

    // 회원탈퇴 - 즐겨찾기 테이블 삭제
    public void deleteFavorite(String userId) {
        log.info("<=====MemberRepository.deleteFavorite=====>");

        memberMapper.deleteFavorite(userId);
    }

    // 회원탈퇴 - 유저차단 테이블 삭제
    public void deleteBlock(String userId) {
        log.info("<=====MemberRepository.deleteBlock=====>");

        memberMapper.deleteBlock(userId);
    }

    // 사용자 차단
    public void addBlock(Block block, String userId, String boardId) {
        log.info("<=====MemberRepository.addBlock=====>");

        memberMapper.addBlock(block, userId, boardId);
    }

    // 사용자 차단해제
    public void cancelBlock(String userId, String blockId) {
        log.info("<=====MemberRepository.cancelBlock=====>");

        memberMapper.cancelBlock(userId, blockId);
    }
}