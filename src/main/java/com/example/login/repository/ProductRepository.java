package com.example.login.repository;

import com.example.login.model.Category;
import com.example.login.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true "
            + "AND (p.id = ?1)")
    Page<Product> listProductByCategory(Integer categoryId, Pageable pageable);

    Optional<Product> findById(Integer id);


    @Query("SELECT p FROM Product p WHERE CONCAT(lower(p.description), ' ', lower(p.name), ' ') LIKE lower(concat('%',?1,'%'))")
    public Page<Product> findAllProduct(String keyword, Pageable pageable);

    List<Product> findAllByNameContainingIgnoreCase(String name);

    @Query("Select p.quantity From Product p WHERE p.enabled = true AND p.id = (?1)")
    Integer getQuantityProduct(Integer productId);

    @Query(value = "SELECT c.product_id FROM product_category c WHERE c.category_id = (?1)", nativeQuery = true)
    List<Integer> getAllIdProductCategories(Integer categoryId);

    @Query("SELECT p FROM Product p WHERE (p.price * ((100 - p.discountPercent)/100)) BETWEEN (?1)*0.9 AND (?1)*1.1 ")
    List<Product> listProductSameMoney(Double money);
}
