package com.example.login.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CartAndProductDto implements Serializable {
    private static final long serialVersionUID = -3822831474371253454L;

    private ProductDto productDto;
    private int quantity;
    private Date checkoutDate;
    private float subTotal;

}
