package com.example.login.controller;

import com.example.login.model.ConfirmationToken;
import com.example.login.model.User;
import com.example.login.model.dto.PasswordResetDto;
import com.example.login.service.ConfirmationTokenService;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;

@Controller
@RequestMapping("reset-password")
public class ResetPasswordController {
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ConfirmationTokenService passwordResetTokenService;
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @GetMapping
//    public String displayResetPasswordPage(@RequestParam(required = false) String token, Model model) {
//        ConfirmationToken resetToken = passwordResetTokenService.findByToken(token);
//        if (resetToken == null){
//            model.addAttribute("error", "Could not find password reset token.");
//        } else if (resetToken.isExpired()){
//            model.addAttribute("error", "Token has expired, please request a new password reset.");
//        } else {
//            model.addAttribute("token", resetToken.getToken());
//        }
//
//        return "reset-password";
//    }
//
//    @PostMapping
//    @Transactional
//    public String handlePasswordReset(@ModelAttribute("passwordResetForm") PasswordResetDto form,
//                                      BindingResult result,
//                                      RedirectAttributes redirectAttributes) {
//
//        if (result.hasErrors()){
//            redirectAttributes.addFlashAttribute(BindingResult.class.getName() + ". passwordResetForm", result);
//            redirectAttributes.addFlashAttribute("passwordResetForm", form);
//            return "redirect:/reset-password?token=" + form.getToken();
//        }
//        if (!form.getPassword().equals(form.getConfirmPassword())){
//            redirectAttributes.addFlashAttribute("passwordResetForm", form);
//            return "redirect:/reset-password?token=" + form.getToken();
//        }
//        ConfirmationToken token = passwordResetTokenService.findByToken(form.getToken());
//        User user = token.getUser();
//        String updatedPassword = passwordEncoder.encode(form.getPassword());
//        userService.updatePassword(user, updatedPassword);
//        passwordResetTokenService.deleteToken(token);
//
//        return "redirect:/login?resetSuccess";
//    }
}
