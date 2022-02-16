package com.example.login.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class CartDto {
    private int id;
    private String statusPayment;
    private UserDto user;

    @Size(max = 50, message = "Name must less than 50 characters")
    @NotBlank(message = "Name cannot empty")
    private String name;

    @Size(max = 50, message = "Address must less than 50 characters")
    @NotBlank(message = "Address cannot empty")
    private String address;

    @Pattern(regexp = "^[\\d]*$", message = "Phone must be digits")
    @Size(max = 15, message = "Phone must be less than 15 characters")
    @NotBlank(message = "Phone cannot empty")
    private String phone;

    @Valid
    private List<CartAndProductDto> cartAndProducts;

    @Getter(AccessLevel.NONE)
    private double totalCost;

    public double getTotalCost() {
        cartAndProducts.forEach(cartAndProductDto -> {
            totalCost += cartAndProductDto.getProduct().getPrice() * cartAndProductDto.getQuantity();
        });
        return totalCost;
    }
}
