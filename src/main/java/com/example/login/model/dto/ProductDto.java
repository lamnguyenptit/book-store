package com.example.login.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductDto {
    private int id;

    @Size(min = 1, max = 50, message = "Name must between 1 and 50 characters")
    @NotBlank(message = "Name can't empty")
    private String name;

    @Pattern(regexp = "^[\\d]*$", message = "Cost must be digits")
    @Size(min = 1, max = 20, message = "Cost must between 1 and 20 characters")
    @NotBlank(message = "Cost can't empty")
    private Float cost;

    private Date createTime;

    private Date updateTime;

    @Pattern(regexp = "^[\\d]*$", message = "Discount percent must be digits")
    @Size(min = 1, max = 2, message = "Discount percent must < 100")
    private Float discountPercent;

    private boolean enabled;

    @Size(max = 4096, message = "Description must less than 4096 characters")
    private String description;

    private String image;

    private boolean inStock;

    @Pattern(regexp = "^[\\d]*$", message = "Cost must be digits")
    @Size(min = 1, max = 9, message = "Cost must between 1 and 9 characters")
    @NotBlank(message = "Quantity can't empty")
    private int quantity;

    @Pattern(regexp = "^[\\d]*$", message = "Cost must be digits")
    @Size(min = 1, max = 20, message = "Cost must between 1 and 20 characters")
    @NotBlank(message = "Price can't empty")
    private Float price;

    @Valid
    private CategoryDto category;

    @Valid
    private PublisherDto publisher;
}
