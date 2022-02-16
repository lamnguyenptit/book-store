package com.example.login.service;

import com.example.login.model.Category;
import com.example.login.model.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<Category> listAllCategoryEnable();

    List<CategoryDto> findAllCategory();

    CategoryDto findById(int id);

    List<CategoryDto> findAllCategoryEnabled();

    void createCategory(CategoryDto categoryDto);

    void updateCategory(CategoryDto categoryDto);

    void deleteCategoryById(int id);
}
