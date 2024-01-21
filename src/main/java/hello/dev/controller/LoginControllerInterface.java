package hello.dev.controller;

import hello.dev.domain.Board;
import hello.dev.domain.Login;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginControllerInterface {

    String loginForm(@ModelAttribute Login login, Board board, Model model, HttpServletRequest request);

    String login(/*@Valid*/ @ModelAttribute Login login, @ModelAttribute Board board
            , BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request
            , RedirectAttributes redirectAttributes, Model model);

    String logout(HttpServletResponse response, HttpServletRequest request);
}
