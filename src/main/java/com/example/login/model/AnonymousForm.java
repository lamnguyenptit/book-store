package com.example.login.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
public class AnonymousForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @NotBlank
    private String address;

//    @Email
//    private String email;
//
//    private boolean sendMail;
}
