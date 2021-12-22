package com.example.login.service;

import com.example.login.model.User;

public interface UserService {
    User checkLogin(String username, String password);
}
