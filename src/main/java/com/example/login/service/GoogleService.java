package com.example.login.service;

import com.example.login.model.GooglePojo;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

public interface GoogleService {
    String getToken(String code) throws IOException;

    GooglePojo getUserInfo(String accessToken) throws IOException;

    UserDetails buildUser(GooglePojo googlePojo);
}
