package com.example.login.model;

import javax.persistence.*;

@Entity
@Table(name = "cart_product")
@IdClass(CartProductId.class)
public class CartAndProduct {

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Id
    @ManyToOne
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Cart cart;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
