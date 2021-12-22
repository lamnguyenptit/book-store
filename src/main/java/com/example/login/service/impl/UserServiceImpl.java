package com.example.login.service.impl;

import com.example.login.model.User;
import com.example.login.repository.UserRepository;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public User checkLogin(String username, String password){
        return userRepository.getUserByUsernameAndAndPassword(username, password).orElse(null);
    }
}
