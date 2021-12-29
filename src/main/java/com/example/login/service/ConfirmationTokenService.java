package com.example.login.service;

import com.example.login.model.ConfirmationToken;
import com.example.login.model.User;

import java.util.Optional;

public interface ConfirmationTokenService {
    void saveConfirmationToken(ConfirmationToken token);

    Optional<ConfirmationToken> findByToken(String token);

    int setConfirmedAt(String token);

    Optional<User> findUserByToken(String token);

    void deleteById(int id);

    void updateConfirmationToken(ConfirmationToken token);

//    void deleteToken(ConfirmationToken token);
}
