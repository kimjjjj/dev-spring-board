package hello.dev.controller;

import hello.dev.domain.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface BoardControllerInterface {

    String chimList(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Login login, @ModelAttribute Board board
            , @RequestParam(required = true, defaultValue = "1") Integer page
            , HttpServletRequest request, Model model);

    String boardList(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, @PathVariable String titleCode
            , @RequestParam(required = true, defaultValue = "1") Integer page
            , Model model, HttpServletRequest request, HttpServletResponse response);

    String boardPost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , Model model, HttpServletRequest request);

    String updateLike(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int seq, @RequestParam String titleCode, Model model);

    String cancelLike(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int seq, @RequestParam String titleCode, Model model);

    String scrapSave(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int seq, @RequestParam String titleCode, Model model);

    String scrapCancel(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable int seq, @RequestParam String titleCode, Model model);

    String addForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , Board board, Model model, HttpServletRequest request);

    String save(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , /*@ModelAttribute("board")*/ Board board, Model model);

    String editForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , Board board, Model model, HttpServletRequest request);

    String updatePost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , Board board, Model model, HttpServletRequest request);

    String deletePost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , Board board, Model model, HttpServletRequest request);

    String search(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, Model model, HttpServletRequest request);

    String mywrite(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, @PathVariable String mypageTitle, Model model, HttpServletRequest request);

    List<BoardCode> boardCodes(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member);

    List<CategoryCode> categoryC(String boardCode);

    String userPage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @ModelAttribute Board board, @PathVariable String boardId, @PathVariable String userPageTitle, Model model);
}
