package com.example.login.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data
public class Degree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
