package com.example.login.model.dto;

import com.example.login.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDto {
    private int id;

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Past(message = "Date must before current")
    @NotNull(message = "Please select date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date admissionDate;

    @Past(message = "Date must before current")
    @NotNull(message = "Please select date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date graduateDate;

    private UserDto user;

    public SchoolDto(String name, Date admissionDate, Date graduateDate, UserDto user) {
        this.name = name;
        this.admissionDate = admissionDate;
        this.graduateDate = graduateDate;
        this.user = user;
    }
}
