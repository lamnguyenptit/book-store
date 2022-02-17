package com.example.login.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartDTO implements Serializable {
    private static final long serialVersionUID = -3822831474371253454L;

    private Integer id;

    private List<CartAndProductDto> cartAndProductDtoList;
    private Float totalMoney;

    public Date getCheckoutDate(){
        return cartAndProductDtoList.get(0).getCheckoutDate();
    }

    public Float getTotalMoney(){
        float total = 0f;
        for (CartAndProductDto cp:
             cartAndProductDtoList) {
            total += cp.getSubTotal();
        }
        return total;
    }

    public String getProductNames(){
        String productNames = "";
        productNames += "<ul>";
        for(CartAndProductDto cp : cartAndProductDtoList){
            productNames += "<li>" + cp.getProductDto().getName() + "</li>";
        }
        productNames += "</ul>";
        return productNames;
    }

    public String getProductNamesToExport(){
        String productNames = "";
        for(CartAndProductDto cp : cartAndProductDtoList){
            productNames += cp.getProductDto().getName();
        }
        return productNames;
    }
}
