package com.example.login.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class CompanyDto {
    private int id;

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Past(message = "Date must before current")
    @NotNull(message = "Please select date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date enteringDate;

    @Past(message = "Date must before current")
    @NotNull(message = "Please select date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date leavingDate;

    private UserDto user;
}
