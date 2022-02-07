package com.example.login.service;

import com.example.login.error.ProductNotFoundException;
import com.example.login.model.Product;
import com.example.login.model.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> listProductByCategory(Integer catId);

    Product getProduct(String id) throws ProductNotFoundException;

    Page<Product> searchProduct(String keyword, int currentPage);

    Page<ProductDto> findAllProduct(Pageable pageable);

    void createProduct(ProductDto productDto);

    void deleteProduct(int id);

    ProductDto findById(int id);

    Page<ProductDto> findProductsByName(Pageable pageable, String name);

    void updateProduct(ProductDto productDto);



    Integer getQuantityProduct(Integer productId);

    List<Product> listProductSameCategory(Integer productId);

    List<Product> listProductSameMoney(int productId);
}
