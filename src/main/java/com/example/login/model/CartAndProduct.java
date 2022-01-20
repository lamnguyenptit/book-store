package com.example.login.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "cart_product")
@IdClass(CartProductId.class)
@Getter
@Setter
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

    @Override
    public String toString() {
        return "CartAndProduct{" +
                "product=" + product.getId() +
                ", cart=" + cart.getId() +
                ", quantity=" + quantity +
                '}';
    }

    public String getProductImage(){
        return product.getImage();
    }

    public Integer getProductId(){
        return product.getId();
    }

//    @Transient
    public Float getSubTotal(){
        return product.getDiscountPrice() * quantity;
    }

    public String getProductName(){
        return product.getName();
    }

    public String getPulisherName(){
        return product.getPublisher().getName();
    }

    public int checkInStock(){

        if(product.isInStock() == false)
            return -1;
        int amount = product.getQuantity();
        if(amount > quantity){
            return 0;}
        else
            return  1;
    }

    public Float getProductPrice(){
        return product.getDiscountPrice();
    }

    public int getRemainingProduct(){
        return product.getQuantity();
    }

}
