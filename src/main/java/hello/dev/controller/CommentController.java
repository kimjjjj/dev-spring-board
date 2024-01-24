package hello.dev.controller;

import hello.dev.domain.*;
import hello.dev.service.BoardService;
import hello.dev.service.CommentService;
import hello.dev.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final MemberService memberService;
    private final BoardService boardService;

    // 댓글 저장
    @PostMapping("/board/{titleCode}/{seq}/parentComment")
    public String saveParentComment(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, @ModelAttribute Comment comment
            , @PathVariable int seq, @RequestParam String content, Model model, HttpServletRequest request) {
        log.info("<=====CommentController.saveParentComment=====> {}, {}", seq, content);

        // 계정 없으면
        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/" + seq);

            return "message";
        }

        if ("".equals(content)) {
            // alert창 띄우기
            model.addAttribute("message", "댓글을 작성해 주세요.");
            model.addAttribute("searchUrl", "/" + seq);

            return "message";
        }

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        model.addAttribute("member", member);

        // 최근방문 게시판 조회
        board = boardService.getCookie(board, request, null);
        model.addAttribute("board", board);

        // 댓글 저장
        commentService.saveParentComment(comment, member.getUserId(), seq, content, 1, 1);

        // 댓글 조회
        List<Comment> comments = commentService.findComment(member.getUserId(), seq);
        model.addAttribute("comments", comments);

        return "redirect:/board/{titleCode}/{seq}";
    }

    // 대댓글 저장&수정
    @PostMapping("/board/{titleCode}/{boardSeq}/childComment")
    public String saveChildComment(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, @ModelAttribute Comment comment
            , @PathVariable int boardSeq, @RequestParam String content, @RequestParam int seq, @RequestParam String editType
            , Model model, HttpServletRequest request) {
        log.info("<=====CommentController.saveChildComment=====>");

        // 계정 없으면
        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/" + boardSeq);

            return "message";
        }

        if ("".equals(content)) {
            // alert창 띄우기
            model.addAttribute("message", "댓글을 작성해 주세요.");
            model.addAttribute("searchUrl", "/" + boardSeq);

            return "message";
        }

        if ("save".equals(editType)) {
            // 상위 댓글의 ORDER_ROW보다 이후 ORDER_ROW가 있으면 +1
            commentService.updateComment(comment.getTopSeq(), comment.getOrderRow());

            // 대댓글 저장
            commentService.saveChildComment(comment, member.getUserId(), boardSeq, content, comment.getTopSeq()
                    , comment.getSeq(), comment.getLvl() + 1, comment.getOrderRow() + 1);
        } else {
            // 댓글 수정
            commentService.editComment(seq, content);
        }

        return "redirect:/board/{titleCode}/{boardSeq}";
    }

    // 댓글 삭제
    @PostMapping("/board/{titleCode}/{seq}/{commentSeq}/delete")
    public String deleteComment(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, @ModelAttribute Comment comment
            , @PathVariable int seq, @PathVariable int commentSeq
            , Model model, HttpServletRequest request) {
        log.info("<=====CommentController.deleteComment=====>");

        // 계정 없으면
        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/login");

            return "message";
        }

        // 댓글 삭제
        commentService.deleteComment(commentSeq);

        return "redirect:/board/{titleCode}/{seq}";
    }

    // 댓글 침하하
    @PostMapping("/{boardSeq}/commentLike")
    public String commentLike(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int boardSeq, @RequestParam int commentSeq
            , @RequestParam String titleCode, Model model) {
        log.info("<=====CommentController.commentLike=====>{}", boardSeq);

        // 계정 없으면
        if (member == null) {
            // alert창 띄우기
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("searchUrl", "/login");

            return "message";
        }

        // 좋아요 테이블 insert
        commentService.commentLike(member.getUserId(), commentSeq);

        // 댓글 포인트 plus
        commentService.updateCommentPoint(commentSeq);

        // 계정 포인트 조회
        member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
        model.addAttribute("member", member);

        return "redirect:/board/" + titleCode + "/{boardSeq}";
    }

    // 댓글 침하하 취소
    @PostMapping("/{boardSeq}/commentCancel")
    public String cancelCommentLike(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int boardSeq, @RequestParam int commentSeq
            , @RequestParam String titleCode, Model model) {
        log.info("<=====CommentController.commentCancel=====>");

        // 계정이 있다면
        if (member != null) {
            // 좋아요 테이블 delete
            commentService.commentCancel(member.getUserId(), commentSeq);

            // 댓글 포인트 minus
            commentService.cancelCommentPoint(commentSeq);

            // 계정 포인트 조회
            member.setUserPoint(memberService.findByUserPoint(member.getUserId()));
            model.addAttribute("member", member);
        }

        return "redirect:/board/" + titleCode + "/{boardSeq}";
    }
}
