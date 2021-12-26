package com.example.login.controller;

import com.example.login.model.Mail;
import com.example.login.model.ConfirmationToken;
import com.example.login.model.User;
import com.example.login.model.dto.PasswordForgotDto;
import com.example.login.service.EmailService;
import com.example.login.service.ConfirmationTokenService;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class ForgotPassWordController {
//    @Autowired
//    private EmailService emailService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ConfirmationTokenService passwordResetTokenService;
//
//    @GetMapping("/forgot-password")
//    public String viewForgotPasswordForm() {
//        return "forgot-password";
//    }
//
//    @PostMapping("/forgot-password")
//    public String processForgotPassword(@ModelAttribute("forgotPasswordForm") @Valid PasswordForgotDto form, HttpServletRequest request, BindingResult result) {
//        if (result.hasErrors()){
//            return "forgot-password";
//        }
//        User user = userService.findUserByEmail(form.getEmail());
//        if (user == null){
//            result.rejectValue("email", null, "We could not find an account for that e-mail address.");
//            return "forgot-password";
//        }
//        ConfirmationToken token = new ConfirmationToken();
//        token.setToken(UUID.randomUUID().toString());
//        token.setUser(user);
//        token.setExpiryDate(30);
//        passwordResetTokenService.save(token);
//
//        Mail mail = new Mail();
//        mail.setFrom("lamnguyenptit99@gmail.com");
//        mail.setTo(user.getEmail());
//        mail.setSubject("Password reset request");
//
//        Map<String, Object> model = new HashMap<>();
//        model.put("token", token);
//        model.put("user", user);
//        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
//        model.put("resetUrl", url + "/reset-password?token=" + token.getToken());
//        mail.setModel(model);
////        emailService.sendEmail(mail);
//        return "redirect:/forgot-password?success";
//    }
}
