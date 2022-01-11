package com.example.login.model;

import lombok.Data;
import lombok.ToString;

@Data
public class GooglePojo {
    private String id;
    private String email;
    private boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String link;
    private String picture;
    private String locale;
}
