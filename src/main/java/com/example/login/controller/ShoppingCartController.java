package com.example.login.controller;

import com.example.login.error.ProductNotFoundException;
import com.example.login.error.ShoppingCartException;
import com.example.login.error.UserNotFoundException;
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
import org.springframework.web.bind.annotation.*;

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
        Boolean checkNullCart = true;
        if(cart == null){
            model.addAttribute("checkNullCart", checkNullCart);
            return "shopping-cart";
        }
        List<CartAndProduct> listProductByUserCart = shoppingCartService.listProductByUserCart(cart.getId());
        Integer totalItem = listProductByUserCart.size();

        if(totalItem <= 0 ){
            model.addAttribute("checkNullCart", checkNullCart);
            return "shopping-cart";
        }

        Float totalMoney = 0.0F;

        for(CartAndProduct products : listProductByUserCart){

            totalMoney += products.getSubTotal();
        }
        checkNullCart = false;
        model.addAttribute("checkNullCart", checkNullCart);
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


    @PostMapping("/cart/add/{productId}/{quantity}")
    @ResponseBody
    public String addToCart(@PathVariable("productId") String productId,
                            @PathVariable("quantity") String quantity, HttpServletRequest request){
//       try{
           User user = getAuthenticatedUser(request);
//           Cart cartByUser = shoppingCartService.getCartByUser(user.getId());
           Integer updateQuantity = shoppingCartService.updateQuantity(user.getId(), Integer.parseInt(productId),
                   Integer.parseInt(quantity));
           return updateQuantity +  " đã cập nhật thành công vào giỏ hàng";
//       }
//       catch (UserNotFoundException ex){
//           return "Bạn cần đăng nhập để thêm sản phẩm này vào giỏ hàng";
//       }
//       catch(ShoppingCartException se){
//           return se.getMessage();
//       }
    }

    @PostMapping("/cart/remove/{productId}")
    @ResponseBody
    public void removeFromCart(@PathVariable("productId") String productId,
                               HttpServletRequest request){
        User user = getAuthenticatedUser(request);
        shoppingCartService.removeProductFromCart(user.getId(), Integer.parseInt(productId));
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    @ResponseBody
    public String updateProductByUser(@PathVariable("productId") String productId,
                                    @PathVariable("quantity") String quantity, HttpServletRequest request){
            User user = getAuthenticatedUser(request);
            Float subTotal = shoppingCartService.updateSubTotal(user.getId(), Integer.parseInt(productId), Integer.parseInt(quantity));
            return String.valueOf(subTotal);
    }

    @GetMapping("/checkout")
    public String checkoutCart(HttpServletRequest request) throws ProductNotFoundException {
        User user = getAuthenticatedUser(request);
        shoppingCartService.checkOutCart(user.getId());
        return "redirect:/view";
    }

    @GetMapping("/purchase")
    public String viewPurchaseOrder(Model model, HttpServletRequest request){
        User user = getAuthenticatedUser(request);
        Boolean checkNullPurchaseOrder = true;
        List<CartAndProduct> listProductPurchase = shoppingCartService.listProductPurchase(user.getId());
        if(listProductPurchase == null){
            model.addAttribute("checkNullPurchaseOrder", checkNullPurchaseOrder);
            return "purchase_order";
        }
        checkNullPurchaseOrder = false;
        model.addAttribute("checkNullPurchaseOrder", checkNullPurchaseOrder);
        model.addAttribute("listProductPurchase", listProductPurchase);
        return "purchase_order";
    }
}
