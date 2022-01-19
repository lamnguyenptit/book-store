package com.example.login.controller;

import com.example.login.model.User;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class AdminController {
    @Autowired
    private UserService userService;

    @RequestMapping( "/admin/home")
    public String viewMainPageAdmin(){
        return "/admin/admin-home-page";
    }

    @GetMapping("/loginAdmin")
    public String viewAdminLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            return "loginAdmin";
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("USER")))
            return "redirect:/user/home";
        return "redirect:/admin/home";
    }

    @GetMapping("/admin/admin")
    public String listAll(Model model){
        List<User> listUsers = userService.listAll();
        model.addAttribute("listUsers", listUsers);
        return "/admin/list-all-users";
    }
}
