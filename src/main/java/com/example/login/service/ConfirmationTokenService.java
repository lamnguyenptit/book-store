package com.example.login.service;

import com.example.login.model.ConfirmationToken;

import java.util.Optional;

public interface ConfirmationTokenService {
    void saveConfirmationToken(ConfirmationToken token);

    Optional<ConfirmationToken> findByToken(String token);

    int setConfirmedAt(String token);

//    void deleteToken(ConfirmationToken token);
}
