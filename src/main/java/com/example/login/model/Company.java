package com.example.login.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Past(message = "Date must before current")
    private Date enteringDate;

    @Past(message = "Date must before current")
    private Date leavingDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
