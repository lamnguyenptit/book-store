package com.example.login.controller;

import com.example.login.model.dto.CartDto;
import com.example.login.model.dto.CategoryDto;
import com.example.login.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    private ShoppingCartService cartService;

    @ModelAttribute("carts")
    public List<CartDto> getCarts(){
        return cartService.findAll();
    }

    @GetMapping("/admin/list-order")
    public String listOrder(){
        return "admin/list-order";
    }

    @GetMapping("/admin/update-order/{id}")
    public String updateOrder(Model model, @PathVariable("id")int id) {
        model.addAttribute("cart", cartService.findById(id));
        return "admin/update-order";
    }

    @PostMapping("/admin/update-order")
    public String processUpdateOrder(@Valid @ModelAttribute("cart")CartDto cartDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "admin/update-order";
        cartService.updateOrder(cartDto);
        return "redirect:/admin/list-order";
    }
}
