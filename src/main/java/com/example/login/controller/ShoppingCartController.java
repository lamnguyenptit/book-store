package com.example.login.controller;

import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.model.Product;
import com.example.login.model.User;
import com.example.login.repository.CartRepository;
import com.example.login.service.ShoppingCartService;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request){
        User user = getAuthenticatedUser(request);
        Cart cart = shoppingCartService.getCartByUser(user.getId());
        List<CartAndProduct> listProductByUserCart = shoppingCartService.listProductByUserCart(cart.getId());

        Float totalMoney = 0.0F;

        for(CartAndProduct products : listProductByUserCart){

            totalMoney += products.getSubTotal();
        }

        model.addAttribute("listProductByUserCart", listProductByUserCart);
        model.addAttribute("totalMoney", totalMoney);
        return "shopping-cart";
    }



    public User getAuthenticatedUser(HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        if(principal == null) return null;
        String userEmail = request.getUserPrincipal().getName();
        return userService.getUserByEmail(userEmail);
    }


}
