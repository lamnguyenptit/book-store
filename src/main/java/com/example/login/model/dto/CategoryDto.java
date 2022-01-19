package com.example.login.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryDto {
    private int id;
    private String name;

    private String image;

    private boolean enabled;
}
