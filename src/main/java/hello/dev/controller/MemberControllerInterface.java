package hello.dev.controller;

import hello.dev.domain.Block;
import hello.dev.domain.Board;
import hello.dev.domain.Member;
import hello.dev.domain.SessionConst;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public interface MemberControllerInterface {

    String joinForm(Board board, Model model, HttpServletRequest request);

    String save(/*@ModelAttribute("board")*/ Member member, @ModelAttribute Board board, Model model, HttpServletRequest request);

    Map<String, String> favorite(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                                        @PathVariable String titleCode, @RequestParam String type, Model model, HttpServletRequest request);

    String mypage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, @ModelAttribute Board board, Model model, HttpServletRequest request);

    String saveMypage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @RequestParam String nickName
            , @ModelAttribute Board board, Model model, HttpServletRequest request) throws ServletException, IOException;

    String delete(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , HttpServletRequest request, Model model);

    String addBlock(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , @ModelAttribute Block block, @RequestParam String boardId, Model model);

    String cancelBlock(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @PathVariable String titleCode, @PathVariable int seq
            , @RequestParam String blockId, Model model);

    String mypageCancelBlock(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , @RequestParam String blockId, Model model);
}
