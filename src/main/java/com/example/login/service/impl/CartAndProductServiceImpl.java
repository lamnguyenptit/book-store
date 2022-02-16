package com.example.login.service.impl;

import com.example.login.model.CartAndProduct;
import com.example.login.repository.CartProductRepository;
import com.example.login.service.CartAndProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartAndProductServiceImpl implements CartAndProductService {
    @Autowired
    private CartProductRepository cartProductRepository;

    @Override
    public CartAndProduct findById(int id) {
        return cartProductRepository.findById(id).orElse(null);
    }
}
