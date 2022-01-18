package com.example.login.service;

import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;

import java.util.List;

public interface ShoppingCartService {

    public Cart getCartByUser(Integer userId);

    public List<CartAndProduct> listProductByUserCart(Integer cartId);
}
