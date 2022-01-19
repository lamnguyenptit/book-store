package com.example.login.controller;

import com.example.login.model.Publisher;
import com.example.login.model.dto.CategoryDto;
import com.example.login.model.dto.ProductDto;
import com.example.login.model.dto.PublisherDto;
import com.example.login.service.CategoryService;
import com.example.login.service.ProductService;
import com.example.login.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PublisherService publisherService;

    @GetMapping("/admin/product")
    public String showProductPage(Model model){
        List<ProductDto> productDtos = productService.findAllProduct();
        model.addAttribute("products", productDtos);
        return "admin/list-product";
    }

    @GetMapping("/admin/add-product")
    public String showAddProductPage(Model model){
        model.addAttribute("product", new ProductDto());

        List<CategoryDto> categoryDtos = categoryService.findAllCategory();
        model.addAttribute("category", categoryDtos);

        List<PublisherDto> publisherDtos = publisherService.findAllPublisher();
        model.addAttribute("publisher", publisherDtos);
        return "admin/add-product";
    }

    @PostMapping("/admin/add-product")
    public String processAddProduct(BindingResult bindingResult, Model model, @ModelAttribute(name = "product")ProductDto productDto, @RequestPart("img") MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()){
            return "admin/add-product";
        }
        System.out.println(productDto);
        if (!multipartFile.isEmpty()) {
            String tail = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            if (tail.equals(".jpg") || tail.equals(".png")) {
                String imageName = productDto.getId() + tail;
                String filePath = Paths.get("").toAbsolutePath() + "/target/classes/static/images/";
                productDto.setImage("images/" + imageName);
                multipartFile.transferTo(Paths.get(filePath + imageName));
            } else {
                String message = "Just accept .jpg or .png file";
                model.addAttribute("messageFile", message);
                return "admin/add-product";
            }
        }
        System.out.println(Float.MAX_VALUE);
        productDto.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        return "admin/add-product";
    }
}
