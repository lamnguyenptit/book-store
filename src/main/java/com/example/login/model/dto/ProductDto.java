package com.example.login.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductDto {
    private int id;

    @Size(min = 1, max = 50, message = "Name must between 1 and 50 characters")
    private String name;

    @Digits(integer = 10, fraction = 5, message = "Cost must be float")
    @NotNull(message = "Please enter cost")
    private Float cost;

    private Date createTime;

    private Date updateTime;

    @Digits(integer = 10, fraction = 5, message = "Discount percent must be float")
    @NotNull(message = "Please enter discount percent")
    private Float discountPercent;

    private boolean enabled;

    @Size(max = 4096, message = "Description must less than 4096 characters")
    private String description;

    private String image;

    private boolean inStock;

    @Digits(integer = 10, fraction = 0, message = "Quantity must be digits")
    @NotNull(message = "Please enter quantity")
    private Integer quantity;

    @Digits(integer = 10, fraction = 5, message = "Cost must be float")
    @NotNull(message = "Please enter price")
    private Float price;

    @Valid
    private CategoryDto category;

    @Valid
    private PublisherDto publisher;
}
