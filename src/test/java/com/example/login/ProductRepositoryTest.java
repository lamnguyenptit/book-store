package com.example.login;

import com.example.login.model.Product;
import com.example.login.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTest {
    @Autowired
    ProductRepository repo;

    @Test
    public void testFindById(){
        Integer id = 1;
        Optional<Product> product = repo.findById(id);

        assertThat(product).isNotNull();
        System.out.printf(product.get().getName());
    }
}
