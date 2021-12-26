package com.example.login.service;

import com.example.login.model.User;

public interface EmailService {
    void sendEmail(User user, String mail);

    boolean isValid(String s);
}
