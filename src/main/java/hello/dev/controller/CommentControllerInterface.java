package hello.dev.controller;

import hello.dev.domain.Board;
import hello.dev.domain.Comment;
import hello.dev.domain.Member;
import hello.dev.domain.SessionConst;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

public interface CommentControllerInterface {

    String saveParentComment(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, @ModelAttribute Comment comment
            , @PathVariable int seq, @RequestParam String content, Model model, HttpServletRequest request);

    String saveChildComment(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, @ModelAttribute Comment comment
            , @PathVariable int boardSeq, @RequestParam String content, @RequestParam int seq, @RequestParam String editType
            , Model model, HttpServletRequest request);

    String deleteComment(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, @ModelAttribute Comment comment
            , @PathVariable int seq, @PathVariable int commentSeq
            , Model model, HttpServletRequest request);

    String commentLike(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int boardSeq, @RequestParam int commentSeq
            , @RequestParam String titleCode, Model model);

    String cancelCommentLike(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int boardSeq, @RequestParam int commentSeq
            , @RequestParam String titleCode, Model model);
}
