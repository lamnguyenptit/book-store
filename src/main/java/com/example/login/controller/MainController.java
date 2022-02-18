package com.example.login.controller;

import com.example.login.error.CategoryNotFoundException;
import com.example.login.error.ProductNotFoundException;
import com.example.login.model.*;
import com.example.login.service.CategoryService;
import com.example.login.service.ProductService;
import com.example.login.service.ShoppingCartService;
import com.example.login.service.impl.ProductServiceImpl;
import com.example.login.service.impl.ShoppingCartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
                                          Model model, RedirectAttributes redirectAttributes){
        try{
            List<Product> listProductByCategory = productService.listProductByCategory(Integer.parseInt(id));
            model.addAttribute("listProductByCategory", listProductByCategory);
            return "view-product-by-category";
        }catch (CategoryNotFoundException cat){
            redirectAttributes.addFlashAttribute("message", "Không thể tìm thấy danh mục có id"+id);
            return "redirect:/view";
        }
    }

    @GetMapping("/p/{id}")
    public String getDetailProduct(@PathVariable(value = "id") String productId,
                                   Model model, RedirectAttributes redirectAttributes) {
        try{
            Product productDetail = productService.getProduct(Integer.parseInt(productId));
            List<Product> listProductSameCategory = productService.listProductSameCategory(Integer.parseInt(productId));
            Set<Category> listAllCategory = productDetail.getCategories();
            List<Product> listProductSameMoney = productService.listProductSameMoney(Integer.parseInt(productId));

            model.addAttribute("listProductSameMoney", listProductSameMoney);
            model.addAttribute("listProductSameCategory", listProductSameCategory);
            model.addAttribute("productDetail", productDetail);
            model.addAttribute("listAllCategory", listAllCategory);
            return "view-detail-product";
        }catch (ProductNotFoundException pr){
            redirectAttributes.addFlashAttribute("message", "Không thể tìm thấy sản phẩm có id"+productId);
            return "redirect:/view";
        }

//        Product productDetail = productService.getProduct(productId);
//        List<Product> listProductSameCategory = productService.listProductSameCategory(Integer.parseInt(productId));
//        List<Product> listProductSameMoney = productService.listProductSameMoney(Integer.parseInt(productId));
//        Set<Category> listAllCategory = productDetail.getCategories();
//        List<Map<String,String>> listCategoryAndProduct = new ArrayList<>();
//        for (Category cat :
//                listAllCategory) {
//            Map<>
//        }

//        model.addAttribute("listProductSameMoney", listProductSameMoney);
//        model.addAttribute("listProductSameCategory", listProductSameCategory);
//        model.addAttribute("productDetail", productDetail);
//        model.addAttribute("listAllCategory", listAllCategory);
//        return "view-detail-product";
    }

    @GetMapping("/search")
    public String firstPageSearch(Model model, @Param("keyword") String keyword){
        return search(model, "1", keyword);
    }

    @GetMapping("/search/page/{pageNum}")
    public String search(Model model, @PathVariable("pageNum") String pageNum, String keyword){
        int currentPage = Integer.parseInt(pageNum);

        Page<Product> page = productService.searchProduct(keyword, currentPage);
        List<Product> listProducts = page.getContent();

        model.addAttribute("totalPages",page.getTotalPages() );
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("moduleURL", "/search");

        int startCount = (currentPage - 1) * ProductServiceImpl.PRODUCT_SEARCH;
        int endCount = Math.min(startCount + ProductServiceImpl.PRODUCT_SEARCH, (int)page.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        return "search-result";
    }

    @GetMapping("/contact")
    public String contactToShop(){
        return "contact";
    }

    @GetMapping("/home")
    public String getHome(HttpServletRequest request, Model model){
        Map<Product, Integer> cartsSession = (Map<Product, Integer>) request.getSession().getAttribute("CARTS_SESSION");
        model.addAttribute("cartsSession", cartsSession != null? cartsSession : new HashMap<>());
        model.addAttribute("product", new Product());
        return "home_addNote";
    }

    @PostMapping("/addNote")
    public String addNewNote(@RequestParam("note") String note, HttpServletRequest request){
        List<String> notes = (List<String>) request.getSession().getAttribute("SESSION_NOTES");
        if(notes == null){
            notes = new ArrayList<>();
            request.getSession().setAttribute("SESSION_NOTES", notes);
        }

        notes.add(note);
        request.getSession().setAttribute("SESSION_NOTES", notes);
        return "redirect:/home";
    }

    @PostMapping("/delete/note")
    public String deleteNote(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:/home";
    }

}
