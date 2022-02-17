package com.example.login.config;

import com.example.login.model.dto.CategoryDto;
import org.springframework.format.Formatter;

import java.util.Locale;

public class CategoryFormatter implements Formatter<CategoryDto> {
    @Override
    public CategoryDto parse(String id, Locale locale) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(Integer.parseInt(id));
        return categoryDto;
    }

    @Override
    public String print(CategoryDto object, Locale locale) {
        return String.valueOf(object.getId());
    }
}
