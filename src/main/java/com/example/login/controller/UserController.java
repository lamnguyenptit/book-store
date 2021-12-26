package com.example.login.controller;

import com.example.login.model.User;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

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

    @GetMapping(value = "/home")
    public String viewMainPage(){
        return "home";
    }

    @GetMapping(value = "/forgot-password")
    public String viewForgotPasswordPage(){
        return "forgot-password";
    }
}
