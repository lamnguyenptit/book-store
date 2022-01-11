package com.example.login.service;

import com.example.login.model.User;
import com.example.login.model.dto.UserDto;

public interface ConverterService {
    UserDto convertToUserDto(User user);
}
