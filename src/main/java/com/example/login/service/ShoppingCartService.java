package com.example.login.service;

import com.example.login.error.ProductNotFoundException;
import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.model.Product;

import java.util.List;

public interface ShoppingCartService {

    public Cart getCartByUser(Integer userId);

    public List<CartAndProduct> listProductByUserCart(Integer cartId);

    public Integer updateQuantity(Integer userId, Integer productId, Integer quantity);

    public CartAndProduct findProductByCart(Integer cartId, Integer productId);

    String removeProductFromCart(int userId, int productId);

    Float updateSubTotal(int userId, int productId, int quantity);

    void checkOutCart(int userId) throws ProductNotFoundException;

    List<CartAndProduct> listProductPurchase(int userId);
}
