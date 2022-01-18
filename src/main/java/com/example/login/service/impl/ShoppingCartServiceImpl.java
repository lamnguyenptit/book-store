package com.example.login.service.impl;

import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.repository.CartProductRepository;
import com.example.login.repository.CartRepository;
import com.example.login.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartProductRepository cartProductRepository;

    @Override
    public Cart getCartByUser(Integer userId) {
        return cartRepository.findCartNotCheckoutByUser(userId);
    }

    @Override
    public List<CartAndProduct> listProductByUserCart(Integer cartId) {
        return cartProductRepository.listProductByUserCart(cartId);
    }
}
