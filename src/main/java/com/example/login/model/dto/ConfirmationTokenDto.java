package com.example.login.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationTokenDto {
    private int id;
    private String token;
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;
    private UserDto user;
}
