package com.example.login.repository;

import com.example.login.model.Cart;
import com.example.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("SELECT c FROM Cart c WHERE c.statusPayment = 'NO'" +
            " AND (c.user.id = (?1))")
    public Cart findCartNotCheckoutByUser(Integer userId);

}
