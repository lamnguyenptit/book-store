package com.example.login.service;

import com.example.login.error.ProductNotFoundException;
import com.example.login.error.ShoppingCartException;
import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ShoppingCartService {

    public Cart getCartByUser(Integer userId);

    public Page<CartAndProduct> listProductByUserCart(Integer cartId, int currentPage, String fieldName, String sortDir);
    public List<CartAndProduct> listProductByUserCart(Integer cartId);

    public Integer updateQuantity(Integer userId, Integer productId, Integer quantity) throws ShoppingCartException;

    public CartAndProduct findProductByCart(Integer cartId, Integer productId);

    String removeProductFromCart(int userId, int productId);

    Float updateSubTotal(int userId, int productId, int quantity);

    void checkOutCart(int userId) throws ProductNotFoundException;

    Page<CartAndProduct> listProductPurchase(int userId, int pageNum, String sortField, String sortDir);

    int getQuantityProductInCart(int userId, int productId);
}
