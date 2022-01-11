package com.example.login.model.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class DegreeDto {
    private int id;

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    private UserDto user;
}
