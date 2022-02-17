package com.example.login.service;

import com.example.login.model.User;

public interface EmailService {
    boolean sendEmailRegister(User user, String mail);

    boolean sendEmailResetPassword(User user, String token);

    boolean isValid(String s);

    void sendEmailUpdateOrder(User user);
}
