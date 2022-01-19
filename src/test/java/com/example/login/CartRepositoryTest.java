package com.example.login;

import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.model.User;
import com.example.login.repository.CartProductRepository;
import com.example.login.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartRepositoryTest {
    @Autowired
    CartRepository repo;

    @Autowired
    CartProductRepository repoCartProduct;

    @Test
    public void testFindCartByUser(){
        Integer id = 1;
        Cart cart = repo.findCartNotCheckoutByUser(id);
        assertThat(cart).isNotNull();
        System.out.printf(cart.toString());
    }

    @Test
    public void listProductByUserCart(){
        Integer cardId = 1;
        List<CartAndProduct> listCart = repoCartProduct.listProductByUserCart(cardId);

        listCart.forEach(System.out::println);
        assertThat(listCart.size()).isEqualTo(2);
    }
}
