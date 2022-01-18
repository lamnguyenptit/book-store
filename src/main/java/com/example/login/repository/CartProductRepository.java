package com.example.login.repository;

import com.example.login.model.CartAndProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartProductRepository extends JpaRepository<CartAndProduct, Integer> {

    @Query("select cp from CartAndProduct cp WHERE (cp.cart.id = (?1))")
    public List<CartAndProduct> listProductByUserCart(Integer cardId);
}
