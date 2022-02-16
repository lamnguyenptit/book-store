package com.example.login.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PublisherDto implements Serializable {
    private static final long serialVersionUID = -3822831474371253454L;
    private int id;
    private String logo;
    private String name;
}
