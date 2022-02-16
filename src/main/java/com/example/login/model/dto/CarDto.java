package com.example.login.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CarDto implements Serializable {
    private static final long serialVersionUID = -3822831474371253454L;

    private Integer id;
    private String statusPayment;
    private UserDto user;

    @Size(max = 50, message = "Name must less than 50 characters")
    @NotBlank(message = "Name cannot empty")
    private String name;

    @Pattern(regexp = "^[\\d]*$", message = "Phone must be digits")
    @Size(max = 15, message = "Phone must be less than 15 characters")
    private String phone;

    @Size(max = 50, message = "Address must less than 50 characters")
    private String address;

    private List<CartAndProductDto> cartAndProducts;

    public Date getCheckoutDate(){
        return cartAndProducts.get(0).getCheckoutDate();
    }

    public Float getTotalMoney(){
        float total = 0f;
        for (CartAndProductDto cp:
                cartAndProducts) {
            total += cp.getSubTotal();
        }
        return total;
    }

    public String getProductNames(){
        String productNames = "";
        productNames += "<ul>";
        for(CartAndProductDto cp : cartAndProducts){
            productNames += "<li>" + cp.getProduct().getName() + "</li>";
        }
        productNames += "</ul>";
        return productNames;
    }
}
