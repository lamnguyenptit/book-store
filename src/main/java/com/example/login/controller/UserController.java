package com.example.login.controller;

import com.example.login.model.User;
import com.example.login.service.ConfirmationTokenService;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping( "/home")
    public String viewMainPage(){
        return "home";
    }

    @GetMapping( "/login")
    public String viewLoginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String viewRegisterPage(Model model){
        model.addAttribute(new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, Model model) {
        String msg = userService.register(user);
        model.addAttribute("msg", msg);
        return "register";
    }

    @RequestMapping("/register/confirm")
    @ResponseBody
    public String confirm(@RequestParam("token") String token) {
        return userService.confirmToken(token);
    }

    @GetMapping( "/forgot-password")
    public String viewForgotPasswordPage(){
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam(value = "email") String email, Model model){
        try {
            String msg = userService.resetPassword(email);
            model.addAttribute("message", msg);
        } catch (UsernameNotFoundException e){
            model.addAttribute("error", e.getMessage());
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password/confirm")
    public String viewResetPasswordPage(@RequestParam(value = "token") String token, Model model) {
        if (!userService.confirmRestPassword(token)){
            model.addAttribute("message", "Cannot change password");
            return "login";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@Param(value = "token") String token, @Param(value = "password") String password,
                                       @Param(value = "repassword") String repw, Model model) {
        if (!password.equals(repw)){
            model.addAttribute("message", "Passwords do not match!");
            return "reset-password";
        }
        if (userService.changePassword(token, password)){
            model.addAttribute("message", "Change password success !");
            return "login";
        }
        model.addAttribute("message", "Cannot reset password !");
        return "reset-password";
    }
}
