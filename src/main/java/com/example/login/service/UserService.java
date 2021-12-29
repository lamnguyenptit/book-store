package com.example.login.service;

import com.example.login.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

public interface UserService extends UserDetailsService {
//    void updatePassword(User user, String password);

    String register(User user);

    String confirmToken(String token);

    String resetPassword(String email);

    boolean confirmRestPassword(String token);

    boolean changePassword(String token, String password);
}
