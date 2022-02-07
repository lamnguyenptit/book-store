package com.example.login.controller;

import com.example.login.error.ProductNotFoundException;
import com.example.login.error.ShoppingCartException;
import com.example.login.error.UserNotFoundException;
import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.model.Product;
import com.example.login.model.User;
import com.example.login.repository.CartRepository;
import com.example.login.service.ProductService;
import com.example.login.service.ShoppingCartService;
import com.example.login.service.UserService;
import com.example.login.service.impl.ShoppingCartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProductService productService;

    @GetMapping("/cart")
    public String viewFirstCart(Model model, HttpServletRequest request) throws UserNotFoundException {
         return viewCart(model, request, "1", "id", "asc");
    }

    @GetMapping("/cart/page/{pageNum}")
    public String viewCart(Model model, HttpServletRequest request,
                           @PathVariable("pageNum") String pageNum, String sortField, String sortDir) throws UserNotFoundException {
        User user = getAuthenticatedUser(request);
        Cart cart = shoppingCartService.getCartByUser(user.getId());

        Boolean checkNullCart = true;
        if(cart == null){
            model.addAttribute("checkNullCart", checkNullCart);
            return "shopping-cart";
        }

        int currentPage = Integer.parseInt(pageNum);
        Page<CartAndProduct> page = shoppingCartService.listProductByUserCart(cart.getId(), currentPage, sortField, sortDir);
        List<CartAndProduct> listProductByUserCart = page.getContent();


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

        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("checkNullCart", checkNullCart);
        model.addAttribute("listProductByUserCart", listProductByUserCart);
        model.addAttribute("totalMoney", totalMoney);
        model.addAttribute("keyword", null);
        model.addAttribute("moduleURL", "/cart");
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        long startCount = (currentPage - 1) * ShoppingCartServiceImpl.PRODUCT_PER_PAGE;
        long endCount = Math.min(startCount + ShoppingCartServiceImpl.PRODUCT_PER_PAGE, page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        return "shopping-cart";
    }


    public User getAuthenticatedUser(HttpServletRequest request) throws UserNotFoundException {
        Object principal = request.getUserPrincipal();
        String userEmail = null;
        if(principal == null) userEmail = null;
        else
            userEmail = request.getUserPrincipal().getName();

        if(userEmail == null)
            throw new UserNotFoundException("Không tồn tại khách hàng!");

        return userService.getUserByEmail(userEmail);
    }


    @PostMapping("/cart/add/{productId}/{quantity}")
    @ResponseBody
    public String addToCart(@PathVariable("productId") String productId, @PathVariable("quantity") String quantity,
                            HttpServletRequest request){
       try{
           User user = getAuthenticatedUser(request);
           if(quantity == null)
               return "Cần nhập số lượng";
           Integer quantityVal;
           try{
               quantityVal = Integer.parseInt(quantity);
           }catch(NumberFormatException e){
               return "Số lượng nhập không đúng định dạng";
           }
//           if(bindingResult.hasErrors()){
//               return "Đã có lỗi xảy ra";
//           }
//           if(quantityVal <= 0)
//               redirectAttributes.addFlashAttribute("message", "Số lượng thêm vào giỏ cần lớn hơn 0!");

           if(quantityVal <= 0)
               return "Số lượng cần lớn hơn 0";
           int quantityProductInCartByUser = shoppingCartService.getQuantityProductInCart(user.getId(),Integer.parseInt(productId));
           int pieceValue = productService.getQuantityProduct(Integer.parseInt(productId));
           if(( quantityVal + quantityProductInCartByUser) > pieceValue)
               return "Tổng số lượng mua lớn hơn số lượng còn lại";

           Integer updateQuantity = shoppingCartService.updateQuantity(user.getId(), Integer.parseInt(productId),
                   quantityVal);
           return updateQuantity +  " sản phẩm đã cập nhật thành công vào giỏ hàng";

       }
       catch (UserNotFoundException ex){
           return "Bạn cần đăng nhập để thêm sản phẩm này vào giỏ hàng";
       }
       catch(ShoppingCartException se){
           return se.getMessage();
       }
    }

    @PostMapping("/cart/remove/{productId}")
    @ResponseBody
    public String removeFromCart(@PathVariable("productId") String productId,
                               HttpServletRequest request){
        try{
            User user = getAuthenticatedUser(request);
            shoppingCartService.removeProductFromCart(user.getId(), Integer.parseInt(productId));
            return "Đã xóa sản phẩm thành công!";
        }catch (UserNotFoundException enf){
            return "Bạn cần phải đăng nhập để loại bỏ sản phẩm";
        }
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    @ResponseBody
    public String updateProductByUser(@PathVariable("productId") String productId,
                                    @PathVariable("quantity") String quantity, HttpServletRequest request){
        try{
            User user = getAuthenticatedUser(request);
            Float subTotal = shoppingCartService.updateSubTotal(user.getId(), Integer.parseInt(productId), Integer.parseInt(quantity));
            return String.valueOf(subTotal);
        }catch (UserNotFoundException unf){
            return "Bạn cần phải đăng nhập để chỉnh sửa";
        }
    }
    @GetMapping("/prepareCheckout")
    @ResponseBody
    public String prepareCheckout(@RequestParam("productId") String productId, @RequestParam("productQuantity") String productQuantity,
                                  @RequestParam("checkIns") String checkIns){
            if(Boolean.valueOf(checkIns) == false)
                return "Không có sẵn trong kho với sản phẩm ID"+productId;
           if(productQuantity == null)
               return "Bạn cần nhập số lượng với sản phẩm ID"+productId;
           Integer quantityVal;
           try{
               quantityVal = Integer.valueOf(productQuantity);
           }catch(NumberFormatException e){
               return "Số lượng nhập không đúng định dạng với sản phẩm ID"+productId;
           }
           if(quantityVal<=0)
               return "Số lượng nhập cần lớn hơn 0 với sản phẩm ID"+productId;
           int pieceValue = productService.getQuantityProduct(Integer.parseInt(productId));
           if(quantityVal > pieceValue)
               return "Tổng số lượng mua lớn hơn số lượng còn lại với sản phẩm ID" + productId;

           return "";
    }


    @GetMapping("/checkout")
    @ResponseBody
    public String checkoutCart(HttpServletRequest request) throws ProductNotFoundException {
        try{
            User user = getAuthenticatedUser(request);
            shoppingCartService.checkOutCart(user.getId());
            return "Bạn đã đặt hàng thành công";
        }catch (UserNotFoundException unf){
            return "Bạn cần phải đăng nhập để thanh toán";
        }
    }


    @GetMapping("/purchase")
    public String firstPagePurchaseOrder(HttpServletRequest request, Model model){

        return viewPurchaseOrder(model, request, "1", "checkoutDate", "desc" );
    }

    @GetMapping("/purchase/page/{pageNum}")
    public String viewPurchaseOrder(Model model, HttpServletRequest request,
                                    @PathVariable("pageNum") String pageNum, String sortField,
                                    String sortDir){
       try{
           int currentPage = Integer.parseInt(pageNum);
           User user = getAuthenticatedUser(request);
           Boolean checkNullPurchaseOrder = true;
           Page<CartAndProduct> page = shoppingCartService.listProductPurchase(user.getId(), currentPage, sortField, sortDir);
           List<CartAndProduct> listProductPurchase = page.getContent();

           if(listProductPurchase == null){
               model.addAttribute("checkNullPurchaseOrder", checkNullPurchaseOrder);
               return "purchase_order";
           }



           checkNullPurchaseOrder = false;
           model.addAttribute("totalPages", page.getTotalPages());
           model.addAttribute("totalItems", page.getTotalElements());
           model.addAttribute("currentPage", currentPage);
           model.addAttribute("listProductPurchase", listProductPurchase);
           model.addAttribute("sortField", sortField);
           model.addAttribute("sortDir", sortDir);
           model.addAttribute("checkNullPurchaseOrder", checkNullPurchaseOrder);
           model.addAttribute("moduleURL", "/purchase");
           model.addAttribute("keyword", null);
           model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

           int startCount =  ShoppingCartServiceImpl.PRODUCT_PER_PAGE * ( currentPage - 1) + 1;
           model.addAttribute("startCount",startCount);

           long endCount = startCount + ShoppingCartServiceImpl.PRODUCT_PER_PAGE + 1;
           if(endCount > page.getTotalElements())
               endCount = page.getTotalElements();
           model.addAttribute("endCount", endCount);

           return "purchase_order";
       }catch(UserNotFoundException une){
           return "Bạn cần phải đăng nhập";
       }
    }
}
