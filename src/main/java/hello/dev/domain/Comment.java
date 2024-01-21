package hello.dev.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {

    private Integer seq;
    private String userId; // id
    private Integer boardSeq; // 게시글 seq
    private String content; // 댓글 내용
    private Integer topSeq; // 게시글 seq
    private Integer parentSeq; // 게시글 seq
    private Integer lvl; // 댓글 레벨
    private Integer orderRow; // 댓글 레벨
    private String insDt; // 입력일자
    private String uptDt; // 수정일자
    private String nickName; // 닉네임
    private String profileName; // 프로필 이미지 이름
    private String profilePath; // 프로필 이미지 저장경로
    private Integer rownumb;
    private String blockId; // 차단한 ID

    private Integer cnt; // 좋아요 유무
    private String editType; // save or update 구분
}
