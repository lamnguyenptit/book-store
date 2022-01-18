package com.example.login.service;

import com.example.login.model.GooglePojo;
import com.example.login.model.User;
import com.example.login.model.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
//    void updatePassword(User user, String password);

    String register(UserDto userDto);

    String confirmToken(String token);

    String resetPassword(String email);

    boolean confirmRestPassword(String token);

    boolean changePassword(String token, String password);

    User processOAuthPostLogin(GooglePojo googlePojo);

    UserDto findByEmail(String email);

    void updateUser(UserDto userDto);
}
