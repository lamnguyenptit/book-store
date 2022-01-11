package com.example.login.service.impl;

import com.example.login.model.User;
import com.example.login.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Override
//    @Async
    public boolean sendEmailRegister(User user, String token) {
        boolean rs = false;
        try {
            Context context = new Context();
            context.setVariable("title", "Verify your email address");
            context.setVariable("content", "Thank you for your register. Please click on the link to verify " +
                    "your email address. The link will expire in 15'.");
            context.setVariable("link","http://localhost:8080/register/confirm?token=" + token);

            String body = templateEngine.process("email/confirm-register", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Email address verification");
            helper.setText(body, true);

            emailSender.send(message);
            rs = true;
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email", e);
        }
        return rs;
    }

    @Override
//    @Async
    public boolean sendEmailResetPassword(User user, String token) {
        boolean rs = false;
        try {
            Context context = new Context();
            context.setVariable("title", "Reset your password");
            context.setVariable("content", "Please click on the link to reset your password. The link will " +
                    "expire in 15'.");
            context.setVariable("link","http://localhost:8080/reset-password/confirm?token=" + token);

            String body = templateEngine.process("email/confirm-register", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Email address reset password");
            helper.setText(body, true);

            emailSender.send(message);
            rs = true;
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email", e);
        }
        return rs;
    }

    @Override
    public boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}
