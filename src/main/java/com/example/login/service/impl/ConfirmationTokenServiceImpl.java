package com.example.login.service.impl;

import com.example.login.model.ConfirmationToken;
import com.example.login.model.User;
import com.example.login.repository.ConfirmationTokenRepository;
import com.example.login.service.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    @Override
    public Optional<ConfirmationToken> findByToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    @Override
    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }

    @Override
    public Optional<User> findUserByToken(String token) {
        return Optional.ofNullable(Objects.requireNonNull(confirmationTokenRepository.findByToken(token).orElse(null)).getUser());
    }

    @Override
    public void deleteById(int id){
        confirmationTokenRepository.deleteById(id);
    }

    @Override
    public void updateConfirmationToken(ConfirmationToken token){
        confirmationTokenRepository.saveAndFlush(token);
    }
}
