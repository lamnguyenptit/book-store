package com.example.login.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UserCartDto implements Serializable {
    private static final long serialVersionUID = -3822831474371253454L;

    private Integer cartId;
    private String name;
    private String phone;
    private String address;
    private Float totalMoney;
    private Date checkoutDate;
}
