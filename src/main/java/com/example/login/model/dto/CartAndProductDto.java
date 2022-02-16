package com.example.login.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter
@Setter
public class CartAndProductDto {
    private int id;
    private ProductDto product;
    private CartDto cart;

    @NotNull(message = "Quantity cannot blank")
    @Digits(integer = 10, fraction = 0, message = "Quantity must be digits")
    @Positive
    @Range(min = 1)
    private int quantity;
    private Date checkoutDate;
    private float subTotal;

    @Override
    public String toString() {
        return "CartAndProductDto{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", checkoutDate=" + checkoutDate +
                ", subTotal=" + subTotal +
                '}';
    }
}
