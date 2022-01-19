package com.example.login.service.impl;

import com.example.login.model.Category;
import com.example.login.model.dto.CategoryDto;
import com.example.login.repository.CategoryRepository;
import com.example.login.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
        return convertToDtos(repo.listAllCategoryEnable());
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
