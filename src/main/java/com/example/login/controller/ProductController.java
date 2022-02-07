package com.example.login.controller;

import com.example.login.model.dto.CategoryDto;
import com.example.login.model.dto.ProductDto;
import com.example.login.model.dto.PublisherDto;
import com.example.login.service.CategoryService;
import com.example.login.service.ProductService;
import com.example.login.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ProductController {
    private static final int PRODUCTS_PER_PAGE = 5;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PublisherService publisherService;

    @GetMapping("/admin/product")
    public String showProductPage(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam(name = "keyword", required = false)String keyword){
//        Optional<Integer> page = Optional.of(pageable.getPageNumber());
//        Optional<Integer> size = Optional.of(pageable.getPageSize());
        int currentPage = page.orElse(1);
        Page<ProductDto> productDtos;
        if (!StringUtils.hasText(keyword))
            productDtos = productService.findAllProduct(PageRequest.of(currentPage - 1, PRODUCTS_PER_PAGE));
        else
            productDtos = productService.findProductsByName(PageRequest.of(currentPage - 1, PRODUCTS_PER_PAGE), keyword);
        model.addAttribute("products", productDtos);
        int totalPage = productDtos.getTotalPages();
        if (totalPage > 0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
//        model.addAttribute("product", new ProductDto());
//        List<CategoryDto> categoryDtos = categoryService.findAllCategory();
//        model.addAttribute("category", categoryDtos);
//        List<PublisherDto> publisherDtos = publisherService.findAllPublisher();
//        model.addAttribute("publisher", publisherDtos);

        model.addAttribute("keyword", keyword);
        return "admin/list-product";
    }

    @GetMapping("/admin/add-product")
    public String showAddProductPage(Model model){
        model.addAttribute("product", new ProductDto());
        model.addAttribute("listCategoryId", new HashSet<Integer>());
        return "admin/add-product";
    }

    @ModelAttribute("categories")
    public List<CategoryDto> getCategories(){
        return categoryService.findAllCategory();
    }

    @ModelAttribute("publishers")
    public List<PublisherDto> getPublishers(){
        return publisherService.findAllPublisher();
    }

    @PostMapping(path = "/admin/add-product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processAddProduct(@Valid @ModelAttribute(name = "product")ProductDto productDto, BindingResult bindingResult,
                                    @ModelAttribute(name = "listCategoryId")HashSet<Integer> listCategoryId, Model model,
                                    @RequestPart("img") MultipartFile multipartFile) throws IOException {
        System.out.println(listCategoryId.size());
        if (bindingResult.hasErrors()){
            return "admin/add-product";
        }
        if (!multipartFile.isEmpty()) {
            String tail = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            if (tail.equals(".jpg") || tail.equals(".png")) {
                String imageName = UUID.randomUUID() + ".jpg";
                String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/product-images/";
                String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/product-images/";
                productDto.setImage(imageName);
                multipartFile.transferTo(Paths.get(filePath1 + imageName));
                multipartFile.transferTo(Paths.get(filePath2 + imageName));
            } else {
                String message = "Just accept .jpg or .png file";
                model.addAttribute("messageFile", message);
                return "admin/add-product";
            }
        }
//        productDto.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
//        productService.createProduct(productDto);
        return "redirect:/admin/add-product?success";
    }

    @GetMapping("/admin/update-product/{id}")
    public String updateProduct(@PathVariable(name = "id")int id, Model model){
        ProductDto productDto = productService.findById(id);
        model.addAttribute("product", productDto);
        List<CategoryDto> categoryDtos = categoryService.findAllCategory();
        model.addAttribute("category", categoryDtos);

        List<PublisherDto> publisherDtos = publisherService.findAllPublisher();
        model.addAttribute("publisher", publisherDtos);
        return "admin/update-product";
    }

    @PostMapping(path = "/admin/update-product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processUpdateProduct(@Valid @ModelAttribute("product")ProductDto productDto, BindingResult bindingResult, @RequestPart("img") MultipartFile multipartFile, Model model) throws IOException {
        if (bindingResult.hasErrors()){
//            ObjectError error = new ObjectError("dateError", "");
//            bindingResult.addError(error);
//            List<ProductDto> productDtos = productService.findAllProduct();
//            model.addAttribute("products", productDtos);
//            model.addAttribute("product", productDto);
            List<CategoryDto> categoryDtos = categoryService.findAllCategory();
            model.addAttribute("category", categoryDtos);
            List<PublisherDto> publisherDtos = publisherService.findAllPublisher();
            model.addAttribute("publisher", publisherDtos);
            return "admin/update-product";
        }
        if (!multipartFile.isEmpty()) {
            String tail = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            if (tail.equals(".jpg") || tail.equals(".png")) {
                String imageName = productDto.getImage();
                if (imageName == null || imageName.equals("")){
                    imageName = UUID.randomUUID() + ".jpg";
                    productDto.setImage(imageName);
                }
                String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/product-images/";
                String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/product-images/";
                multipartFile.transferTo(Paths.get(filePath1 + imageName));
                multipartFile.transferTo(Paths.get(filePath2 + imageName));
            } else {
                String message = "Just accept .jpg or .png file";
                model.addAttribute("messageFile", message);
                return "admin/update-product";
            }
        }
        productService.updateProduct(productDto);
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/delete-product")
    @ResponseBody
    public void deleteProduct(@RequestParam("id") int id){
        productService.deleteProduct(id);
    }
}
