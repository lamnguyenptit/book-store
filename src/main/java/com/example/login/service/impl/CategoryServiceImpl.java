package com.example.login.service.impl;

import com.example.login.model.Category;
import com.example.login.model.Product;
import com.example.login.model.dto.CategoryDto;
import com.example.login.repository.CategoryRepository;
import com.example.login.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository repo;

    @Override
    public List<Category> listAllCategoryEnable() {
        return repo.listAllCategoryEnable();
    }

    @Override
    public List<CategoryDto> findAllCategory() {
        return convertToDtos(repo.findAll());
    }

    @Override
    public CategoryDto findById(int id){
        return convertToDto(repo.findById(id).orElse(null));
    }

    @Override
    public List<CategoryDto> findAllCategoryEnabled() {
        return convertToDtos(repo.listAllCategoryEnable());
    }

    @Override
    public void createCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setImage(categoryDto.getImage());
        category.setName(categoryDto.getName());
        category.setEnabled(categoryDto.isEnabled());
        repo.save(category);
    }

    @Override
    public void updateCategory(CategoryDto categoryDto) {
        Category category = repo.findById(categoryDto.getId()).orElse(null);
        if (category == null)
            return;
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setEnabled(categoryDto.isEnabled());
        if (category.getImage() == null || category.getImage().equals(""))
            category.setImage(categoryDto.getImage());
        repo.saveAndFlush(category);
    }

    @Override
    public void deleteCategoryById(int id) {
        Category category = repo.findById(id).orElse(null);
        if (category == null)
            return;
        if (category.getImage() != null || !category.getImage().equals("")){
            String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/category-images/";
            String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/category-images/";
            try {
                Files.delete(Paths.get(filePath1 + category.getImage()));
                Files.delete(Paths.get(filePath2 + category.getImage()));
            } catch (IOException ignored) {
            }
            try {
                Files.delete(Paths.get(filePath2 + category.getImage()));
            } catch (IOException ignored) {
            }
        }
        repo.delete(category);
    }

    public CategoryDto convertToDto(Category category){
        if (category == null)
            return null;
        CategoryDto categoryDto = new CategoryDto();
        BeanUtils.copyProperties(category, categoryDto);
        return categoryDto;
    }

    public List<CategoryDto> convertToDtos(List<Category> categories){
        if (categories == null)
            return null;
        return categories.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
