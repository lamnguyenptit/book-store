package com.example.login.controller;

import com.example.login.model.dto.CategoryDto;
import com.example.login.model.dto.ProductDto;
import com.example.login.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/admin/list-category")
    public String listAllCategory(Model model){
        model.addAttribute("categories", categoryService.findAllCategory());
        return "admin/list-category";
    }

    @GetMapping("/admin/add-category")
    public String addCategory(Model model) {
        model.addAttribute("category", new CategoryDto());
        return "admin/add-category";
    }

    @PostMapping(path = "/admin/add-category", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processAddCategory(@Valid @ModelAttribute(name = "category") CategoryDto categoryDto, BindingResult bindingResult,
                                     Model model, @RequestPart("img") MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()){
            return "admin/add-category";
        }
        if (!multipartFile.isEmpty()) {
            String tail = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            if (tail.equals(".jpg") || tail.equals(".png")) {
                String imageName = UUID.randomUUID() + ".jpg";
                String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/category-images/";
                String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/category-images/";
                categoryDto.setImage(imageName);
                multipartFile.transferTo(Paths.get(filePath1 + imageName));
                multipartFile.transferTo(Paths.get(filePath2 + imageName));
            } else {
                String message = "Just accept .jpg or .png file";
                model.addAttribute("messageFile", message);
                return "admin/add-category";
            }
        }
        categoryService.createCategory(categoryDto);
        return "redirect:/admin/add-category?success";
    }

    @GetMapping("/admin/update-category/{id}")
    public String updateCategory(@PathVariable(name = "id")int id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        return "admin/update-category";
    }

    @PostMapping(path = "/admin/update-category", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processUpdateCategory(@Valid @ModelAttribute(name = "category") CategoryDto categoryDto, BindingResult bindingResult,
                                     Model model, @RequestPart("img") MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()){
            return "admin/update-category";
        }
        if (!multipartFile.isEmpty()) {
            String tail = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            if (tail.equals(".jpg") || tail.equals(".png")) {
                String imageName = categoryDto.getImage();
                if (imageName == null || imageName.equals("")){
                    imageName = UUID.randomUUID() + ".jpg";
                    categoryDto.setImage(imageName);
                }
                String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/category-images/";
                String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/category-images/";
                multipartFile.transferTo(Paths.get(filePath1 + imageName));
                multipartFile.transferTo(Paths.get(filePath2 + imageName));
            } else {
                String message = "Just accept .jpg or .png file";
                model.addAttribute("messageFile", message);
                return "admin/add-category";
            }
        }
        categoryService.updateCategory(categoryDto);
        return "redirect:/admin/list-category";
    }

    @GetMapping("/admin/delete-category")
    @ResponseBody
    public void deleteProduct(@RequestParam("id") int id){
        categoryService.deleteCategoryById(id);
    }
}
