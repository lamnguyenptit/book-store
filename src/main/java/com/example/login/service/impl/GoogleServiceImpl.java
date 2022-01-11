package com.example.login.service.impl;

import com.example.login.model.GooglePojo;
import com.example.login.model.Role;
import com.example.login.model.User;
import com.example.login.service.GoogleService;
import com.example.login.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleServiceImpl implements GoogleService {
    @Autowired
    private Environment evn;

    @Autowired
    private UserService userService;

    @Override
    public String getToken(final String code) throws IOException {
        String link = evn.getProperty("google.link.get.token");
        String response = Request.Post(link)
                .bodyForm(Form.form().add("client_id", evn.getProperty("google.app.id"))
                        .add("client_secret", evn.getProperty("google.app.secret"))
                        .add("redirect_uri", evn.getProperty("google.redirect.uri")).add("code", code)
                        .add("grant_type", "authorization_code").build())
                .execute().returnContent().asString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response).get("access_token");
        return node.textValue();
    }

    @Override
    public GooglePojo getUserInfo(final String accessToken) throws IOException {
        String link = evn.getProperty("google.link.get.user_info") + accessToken;
        String response = Request.Get(link).execute().returnContent().asString();
        ObjectMapper mapper = new ObjectMapper();
        GooglePojo googlePojo = mapper.readValue(response, GooglePojo.class);
        userService.processOAuthPostLogin(googlePojo);
        return googlePojo;
    }

    @Override
    public UserDetails buildUser(GooglePojo googlePojo) {
        return new User(googlePojo.getName(), googlePojo.getEmail(), "", Role.USER);
    }
}
