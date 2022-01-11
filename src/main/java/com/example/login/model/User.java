package com.example.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    @Email(message = "Email isn't valid!")
    private String email;

    @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters")
    private String password;

    @Size(max = 50, message = "Name must less than 50 characters")
    @NotBlank(message = "Name can't empty")
    private String name;

    private String phone;

    @Size(max = 50, message = "Address must less than 50 characters")
    private String address;
    private String image;

    @OneToMany(mappedBy = "user")
    @Valid
    private List<Company> companies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Valid
    private List<School> schools;

    @OneToMany(mappedBy = "user")
    @Valid
    private List<Degree> degrees;

    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean locked;
    private boolean enabled;

    @OneToOne(mappedBy = "user")
    private ConfirmationToken confirmationToken;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    public User(String name, String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
        this.locked = false;
        this.enabled = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
