package com.example.login.service;

import com.example.login.error.ProductNotFoundException;
import com.example.login.model.Product;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ProductService {

    Page<Product> listProductByCategory(Integer catId);

    Product getProduct(String id) throws ProductNotFoundException;

    Page<Product> searchProduct(String keyword);
}
