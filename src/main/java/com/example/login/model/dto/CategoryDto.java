package com.example.login.model.dto;

import com.example.login.model.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CategoryDto {
    private int id;
    private String name;

    private String image;

    private boolean enabled;
}
