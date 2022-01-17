package com.example.login.model.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
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

    @Override
    public String toString() {
        return "SchoolDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", admissionDate=" + admissionDate +
                ", graduateDate=" + graduateDate +
                ", user=" + user.getId() +
                '}';
    }
}
