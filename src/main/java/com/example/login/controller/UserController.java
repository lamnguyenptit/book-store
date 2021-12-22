package com.example.login.controller;

import com.example.login.model.User;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/")
    public String login(){
        return "login";
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam String username, @RequestParam String password){
        User user = userService.checkLogin(username, password);
        if (user == null)
            return "login";
        return "trangchu";
    }
}
