package com.example.login;

import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.model.Product;
import com.example.login.model.User;
import com.example.login.repository.CartProductRepository;
import com.example.login.repository.CartRepository;
import com.example.login.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartRepositoryTest {
    @Autowired
    CartRepository repo;

    @Autowired
    CartProductRepository repoCartProduct;

    @Autowired
    ProductRepository productRepository;

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

    @Test
    public void testRemoveProductByUser(){
        Integer cartId = 1;
        Integer productId = 1;

        repoCartProduct.removeProductByCart(cartId, productId);

        CartAndProduct result = repoCartProduct.findProductByCart(cartId, productId);
        assertThat(result).isNull();
    }

    @Test
    public void testSearchProduct(){
        String keyword = "h";
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Product> page = productRepository.findAllProduct(keyword, pageable);
        List<Product> listProducts = page.getContent();
        listProducts.forEach(product -> System.out.println(product));
        Integer totalItem = Math.toIntExact(page.getTotalElements());
        System.out.println(totalItem);

        assertThat(listProducts.size()).isGreaterThan(0);
    }
}
