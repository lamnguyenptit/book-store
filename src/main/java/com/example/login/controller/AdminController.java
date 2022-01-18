package com.example.login.controller;

import com.example.login.model.User;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
//@RequestMapping("/adminSite")
public class AdminController {
    @Autowired
    private UserService userService;


    @GetMapping("/adminSite")
    public String viewAdminHomePage(){
        return "/admin/admin-home-page";
    }

    @GetMapping("/adminSite/users")
    public String listAll(Model model){
        List<User> listUsers = userService.listAll();
        model.addAttribute("listUsers", listUsers);
        return "/admin/list-all-users";
    }
}
