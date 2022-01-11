package com.example.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
