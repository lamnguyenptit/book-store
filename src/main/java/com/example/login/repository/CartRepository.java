package com.example.login.repository;

import com.example.login.model.Cart;
import com.example.login.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("SELECT c FROM Cart c WHERE c.statusPayment = 'NO'" +
            " AND (c.user.id = (?1))")
    public Cart findCartNotCheckoutByUser(Integer userId);

    @Modifying
    @Query("UPDATE Cart c SET c.statusPayment = 'YES' WHERE c.statusPayment = 'NO'" +
            " AND c.user.id = (?1)")
    void checkOutCart(Integer userId);

    @Query("SELECT c FROM Cart c WHERE c.statusPayment = 'YES'" +
            " AND c.user.id = (?1)")
    List<Cart> listCartPurchase(int userId);

    @Query("SELECT c FROM Cart c WHERE c.statusPayment = 'DELAY' AND c.user.id = (?1)")
    Cart findCartDelayByUser(int userId);

    @Modifying
    @Query("UPDATE Cart c SET c.statusPayment = 'YES' WHERE c.statusPayment = 'DELAY'" +
            " AND c.user.id = (?1)")
    void updateSuccessCheckoutCart(int userId);
}
