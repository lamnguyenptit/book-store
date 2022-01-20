package com.example.login.repository;

import com.example.login.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true "
            + "AND (p.category.id = ?1)")
    Page<Product> listProductByCategory(Integer categoryId, Pageable pageable);

    Optional<Product> findById(Integer id);


    @Query("SELECT p FROM Product p WHERE p.enabled = true AND " +
            "p.description like 'chí'")
    public Page<Product> search(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE lower(p.description) LIKE lower(concat('%',?1,'%'))")
    public Page<Product> findAllProduct(String keyword, Pageable pageable);
}
