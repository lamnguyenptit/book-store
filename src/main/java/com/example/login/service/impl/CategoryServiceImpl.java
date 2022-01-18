package com.example.login.service.impl;

import com.example.login.model.Category;
import com.example.login.repository.CategoryRepository;
import com.example.login.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository repo;

    @Override
    public List<Category> listAllCategoryEnable() {
        return repo.listAllCategoryEnable();
    }
}
