package com.example.login.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CategoryDto implements Serializable {
    private static final long serialVersionUID = -3822831474371253454L;
    private int id;
    private String name;

    private String image;

    private boolean enabled;
}
