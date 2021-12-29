package com.example.login.service.impl;

import com.example.login.model.*;
import com.example.login.repository.UserRepository;
import com.example.login.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final static String USER_NOT_FOUND_MSG = "User with email: %s not found";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    @Override
    public String register(User param) {
        if (!emailService.isValid(param.getEmail())) {
           return "Email not valid";
        }
        User user = userRepository.findByEmail(param.getEmail()).orElse(null);
        if (user != null) {
            if (user.isEnabled()) {
                return "Cannot send your email";
            }
            String token = updateToken(user);
            if (!emailService.sendEmailRegister(user, token)){
                return "Cannot send your email";
            }
        }
        else {
            param.setPassword(bCryptPasswordEncoder.encode(param.getPassword()));
            User user1 = new User(param.getName(), param.getEmail(), param.getPassword(), Role.USER);
            userRepository.save(user1);
            String token = generateToken(user1);
            if (!emailService.sendEmailRegister(user1, token)){
                return "Cannot send your email";
            }
        }
        return "An email was sent to your email address. Please verify your email!";
    }

    private String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    private String updateToken(User user){
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                user.getConfirmationToken().getId(),
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        confirmationTokenService.updateConfirmationToken(confirmationToken);
        return token;
    }

    @Override
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token).orElse(null);

        if (confirmationToken == null)
            return "Wrong token";

        if (confirmationToken.getConfirmedAt() != null) {
            return "Error";
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            return "Token expired";
        }

        confirmationTokenService.setConfirmedAt(token);
        userRepository.enableUser(confirmationToken.getUser().getEmail());
        return "Confirmed";
    }

    @Override
    public String resetPassword(String email) {
        if (!emailService.isValid(email)) {
            return "Invalid email";
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            if (!user.isEnabled())
                return "Account doesn't exist!";
            String token = updateToken(user);
            if (emailService.sendEmailResetPassword(user, token))
                return "We have sent a reset password link to your email. Please check.";
        }
        return "Cannot send to your email";
    }

    @Override
    public boolean confirmRestPassword(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token).orElse(null);

        if (confirmationToken == null)
            return false;

        if (confirmationToken.getConfirmedAt() != null) {
            return false;
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        confirmationTokenService.setConfirmedAt(token);
        return true;
    }

    @Override
    public boolean changePassword(String token, String password) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token).orElse(null);
        if (confirmationToken == null)
            return false;

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = confirmationTokenService.findUserByToken(token).orElse(null);
        if (user == null)
            return false;
        userRepository.updatePassword(bCryptPasswordEncoder.encode(password), user.getId());
        return true;
    }
}
