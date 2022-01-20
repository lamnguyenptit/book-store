package com.example.login.repository;

import com.example.login.model.CartAndProduct;
import com.example.login.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartProductRepository extends JpaRepository<CartAndProduct, Integer> {

    @Query("select cp from CartAndProduct cp WHERE (cp.cart.id = (?1))")
    public List<CartAndProduct> listProductByUserCart(Integer cardId);

    @Query("select cp from CartAndProduct cp WHERE (cp.cart.id = (?1))" +
            " AND (cp.product.id = (?2))")
    CartAndProduct findProductByCart(Integer cartId, Integer productId);

    @Modifying
    @Query("DELETE FROM CartAndProduct cp where cp.cart.id=(?1)" +
            " AND cp.product.id=?2")
    void removeProductByCart(Integer cartId, int productId);

}
