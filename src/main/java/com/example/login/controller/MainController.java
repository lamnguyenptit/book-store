package com.example.login.controller;

import com.example.login.error.ProductNotFoundException;
import com.example.login.model.Category;
import com.example.login.model.Product;
import com.example.login.repository.CategoryRepository;
import com.example.login.service.CategoryService;
import com.example.login.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.model.IModel;

import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/view")
    public String viewHomePage(Model model){
        List<Category> listCategories = categoryService.listAllCategoryEnable();
        model.addAttribute("listCategories", listCategories);
        return "index-home-page";
    }

    @GetMapping("/c/{id}")
    public String getAllProductInCategory(@PathVariable(value = "id") String id,
                                          Model model){
        List<Product> listProductByCategory = productService.listProductByCategory(Integer.parseInt(id));
        model.addAttribute("listProductByCategory", listProductByCategory);
        return "view-product-by-category";
    }

    @GetMapping("/p/{id}")
    public String getDetailProduct(@PathVariable(value = "id") String productId,
                                  Model model) throws ProductNotFoundException {
        Product productDetail = productService.getProduct(productId);
        List<Product> listProductSameCategory = productService.listProductSameCategory(Integer.parseInt(productId));
        List<Product> listProductSameMoney = productService.listProductSameMoney(Integer.parseInt(productId));

        model.addAttribute("listProductSameMoney", listProductSameMoney);
        model.addAttribute("listProductSameCategory", listProductSameCategory);
        model.addAttribute("productDetail", productDetail);
        return "view-detail-product";
    }

    @GetMapping("/search")
    public String search(@Param("keyword") String keyword, Model model){
        Page<Product> pageProducts = productService.searchProduct(keyword);
        List<Product> listProducts = pageProducts.getContent();
        Integer totalItem = Math.toIntExact(pageProducts.getTotalElements());

        model.addAttribute("pageProducts",pageProducts );
        model.addAttribute("totalItems", totalItem);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listProducts", listProducts);
        return "search-result";
    }

    @GetMapping("/contact")
    public String contactToShop(){
        return "contact";
    }
}
