package com.example.login.service.impl;

import com.example.login.error.ProductNotFoundException;
import com.example.login.model.Product;
import com.example.login.repository.ProductRepository;
import com.example.login.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repo;

    @Override
    public Page<Product> listProductByCategory(Integer catId) {
        Pageable pageable = PageRequest.of(0, 10);
        return repo.listProductByCategory(catId, pageable);
    }

    @Override
    public Product getProduct(String id) throws ProductNotFoundException {
        Optional<Product> productOptional = repo.findById(Integer.parseInt(id));
        return productOptional
                .orElseThrow(() -> new ProductNotFoundException("không thể tìm thấy sản phẩm"));
    }

    @Override
    public Page<Product> searchProduct(String keyword) {
        Pageable pageable = PageRequest.of(0,10);
        return repo.search(keyword, pageable);
    }
}