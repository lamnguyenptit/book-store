package com.example.login.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="admins")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Admin extends IdBaseEntity {

    @Column(name="email", length = 128, nullable = false, unique = true)
    private String email;

    @Column(name="password", length = 128, nullable = false)
    private String password;

    @Column(name="name", length = 50, nullable = false)
    private String name;

    private boolean enabled;

    @Column(length = 256)
    private String photo;

    @Column(name="check_admin", length = 25, nullable = false)
    private String checkAdmin;
}
