package com.example.login.repository;

import com.example.login.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /*////
     - @Query
     - 1 dau bang
     - Category là class
     */
    @Query("SELECT c FROM Category c WHERE c.enabled = true")
    List<Category> listAllCategoryEnable();
}
