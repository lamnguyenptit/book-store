package com.example.login.controller;

import com.example.login.model.dto.CategoryDto;
import com.example.login.model.dto.ProductDto;
import com.example.login.model.dto.PublisherDto;
import com.example.login.service.CategoryService;
import com.example.login.service.ProductService;
import com.example.login.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        model.addAttribute("product", new ProductDto());
        List<CategoryDto> categoryDtos = categoryService.findAllCategory();
        model.addAttribute("category", categoryDtos);

        List<PublisherDto> publisherDtos = publisherService.findAllPublisher();
        model.addAttribute("publisher", publisherDtos);
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

    @PostMapping(path = "/admin/add-product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processAddProduct(@Valid @ModelAttribute(name = "product")ProductDto productDto,BindingResult bindingResult, Model model, @RequestPart("img") MultipartFile multipartFile) throws IOException {
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
        productDto.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        productService.createProduct(productDto);
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
