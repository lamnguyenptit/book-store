package com.example.login.service;

import com.example.login.model.CheckoutType;
import com.example.login.model.GooglePojo;
import com.example.login.model.User;
import com.example.login.model.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

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

    User getUserById(Integer id);

    List<UserDto> listAllAdmin();

    void delete(Integer id);

    User getUserByEmail(String userEmail);

    void deleteAdmin(int id);

    int countAdmin();

    void createAdmin(UserDto userDto);

    UserDto findAdminById(int id);

    void updateAdmin(UserDto userDto);

    List<UserDto> listAllUser();

    void enableUserById(int id);

    void lockUserById(int id);

    void changeAdminPassword(UserDto userDto);
}
