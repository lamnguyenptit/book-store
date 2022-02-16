package com.example.login.controller;

import com.example.login.error.ProductNotFoundException;
import com.example.login.error.ShoppingCartException;
import com.example.login.error.UserNotFoundException;
import com.example.login.model.*;
import com.example.login.model.dto.CartAndProductDto;
import com.example.login.model.dto.CarDto;
import com.example.login.model.dto.ProductDto;
import com.example.login.model.dto.UserCartDto;
import com.example.login.service.ProductService;
import com.example.login.service.ShoppingCartService;
import com.example.login.service.UserService;
import com.example.login.service.impl.ShoppingCartServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ShoppingCartController {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private JavaMailSender emailSender;

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
        User user = userService.getUserByEmail(userEmail);

        return userService.getUserByEmail(userEmail);
    }


    @PostMapping("/cart/add/{productId}/{quantity}")
    @ResponseBody
    public String addToCart(@PathVariable("productId") String productId, @PathVariable("quantity") String quantity,
                            HttpServletRequest request){
       try{
           User user = getAuthenticatedUser(request);
           if(checkProductFound(Integer.parseInt(productId)) == false)
               return "Sản phẩm không tồn tại";
           if(user.getRole().equals(Role.ADMIN))
               return "Bạn cần đăng nhập để thêm sản phẩm này vào giỏ hàng";
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

           if(( quantityVal + quantityProductInCartByUser) > pieceValue) {
               if(quantityProductInCartByUser > 0)
                   return "Đã có "+quantityProductInCartByUser+" sản phẩm trong giỏ\n" +
                           "Số lượng có thể mua tiếp tối đa là " + (pieceValue - quantityProductInCartByUser) + " sản phẩm";
               return "Tổng số lượng mua lớn hơn số lượng còn lại";
           }
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



    @GetMapping("/cartAnonymous")
    public String viewCartAnonymous(Model model, HttpServletRequest request, HttpSession session) throws ProductNotFoundException {
        Boolean checkNullCart = true;

        Map<ProductDto, Integer> cartsSession = (Map<ProductDto, Integer>)session.getAttribute("CARTS_SESSION");

        if(cartsSession == null || cartsSession.size() == 0){
            model.addAttribute("checkNullCart", checkNullCart);
        return "shopping-cartAnonymous";
        }

        Float totalMoney = 0.0F;
        for(Map.Entry<ProductDto, Integer> element : cartsSession.entrySet()){
//            Product product = productService.getProduct(element.getKey().getId());
//            ProductDto productDto = productService.convertToProductDto(product);
//
            ProductDto productDto = element.getKey();
            productDto.setOrderQuantity(element.getValue());
            totalMoney += productDto.getSubTotal();
            cartsSession.put(productDto, element.getValue());
        }

        request.getSession().setAttribute("CARTS_SESSION", cartsSession);

        checkNullCart = false;
        model.addAttribute("totalMoney", totalMoney);
        model.addAttribute("checkNullCart", checkNullCart);
        model.addAttribute("cartsSession", cartsSession);

        return "shopping-cartAnonymous";

//        return viewCartAnonymousPage(model, request, "1", "id", "asc");
    }

    @GetMapping("/cartAnonymous/page/{pageNum}")
    public String viewCartAnonymousPage(Model model, HttpServletRequest request,
                                        @PathVariable("pageNum") String pageNum, String sortField, String sortDir) {
        Boolean checkNullCart = true;

        Map<ProductDto, Integer> cartsSession = (Map<ProductDto, Integer>)request.getSession().getAttribute("CARTS_SESSION");
        if(cartsSession == null){
            model.addAttribute("checkNullCart", checkNullCart);
            return "shopping-cartAnonymous";
        }


//        int currentPage = Integer.parseInt(pageNum);
//
//        Page<CartAndProduct> page = shoppingCartService.listProductByUserCart(cart.getId(), currentPage, sortField, sortDir);
//
//
//        List<CartAndProduct> listProductByUserCart = page.getContent();
//
//
//        Integer totalItem = listProductByUserCart.size();
//
//        if(totalItem <= 0 ){
//            model.addAttribute("checkNullCart", checkNullCart);
//            return "shopping-cart";
//        }
//
//        Float totalMoney = 0.0F;
//
//        for(CartAndProduct products : listProductByUserCart){
//            totalMoney += products.getSubTotal();
//        }
//        checkNullCart = false;
//
//        model.addAttribute("totalItems", page.getTotalElements());
//        model.addAttribute("totalPages", page.getTotalPages());
//        model.addAttribute("currentPage", currentPage);
//        model.addAttribute("sortField", sortField);
//        model.addAttribute("sortDir", sortDir);
//        model.addAttribute("checkNullCart", checkNullCart);
//        model.addAttribute("listProductByUserCart", listProductByUserCart);
//        model.addAttribute("totalMoney", totalMoney);
//        model.addAttribute("keyword", null);
//        model.addAttribute("moduleURL", "/cart");
//        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
//        long startCount = (currentPage - 1) * ShoppingCartServiceImpl.PRODUCT_PER_PAGE;
//        long endCount = Math.min(startCount + ShoppingCartServiceImpl.PRODUCT_PER_PAGE, page.getTotalPages());
//        model.addAttribute("startCount", startCount);
//        model.addAttribute("endCount", endCount);

        model.addAttribute("cartsSession", cartsSession);

        return "shopping-cartAnonymous";
    }

    @PostMapping("/cartAnonymous/add/{productId}/{quantity}")
    @ResponseBody
    public String addToCartAnonymous(@PathVariable("productId") String productId, @PathVariable("quantity") String quantity,
                                     @NotNull HttpServletRequest request) throws ProductNotFoundException {
        Map<ProductDto, Integer> cartsSession = (Map<ProductDto, Integer>) request.getSession().getAttribute("CARTS_SESSION");

        if(cartsSession == null){
            cartsSession = new HashMap<>();
            request.getSession().setAttribute("CARTS_SESSION", cartsSession);
        }

        if(checkProductFound(Integer.parseInt(productId)) == false)
            return "Sản phẩm không tồn tại";
        if(quantity == null)
            return "Cần nhập số lượng";
        Integer quantityVal;
        try{
            quantityVal = Integer.parseInt(quantity);
        }catch(NumberFormatException e){
            return "Số lượng nhập không đúng định dạng";
        }
        if(quantityVal <= 0)
            return "Số lượng cần lớn hơn 0";

//        Product product = new Product(Integer.parseInt(productId));
        Product product = productService.getProduct(Integer.parseInt(productId));
        ProductDto productDto = productService.convertToProductDto(product);

        Integer numberProductAnonymous = cartsSession.get(productDto) == null? 0 : cartsSession.get(productDto);

        int pieceValue = productService.getQuantityProduct(Integer.parseInt(productId));

        if(( quantityVal + numberProductAnonymous) > pieceValue) {
            if(numberProductAnonymous > 0)
                return "Đã có "+numberProductAnonymous+" sản phẩm trong giỏ\n" +
                        "Số lượng có thể mua tiếp tối đa là " + (pieceValue - numberProductAnonymous) + " sản phẩm";
            return "Tổng số lượng mua lớn hơn số lượng còn lại";
        }

        Integer updateQuantity = numberProductAnonymous + quantityVal;
        productDto.setOrderQuantity(updateQuantity);
        System.out.printf("\norder:  "+ productDto.getOrderQuantity());
        System.out.printf("\nsdjfkdf: "+ productDto.getSubTotal());

        cartsSession.put(productDto, updateQuantity);

        request.getSession().setAttribute("CARTS_SESSION", cartsSession);
        return updateQuantity +  " sản phẩm đã cập nhật thành công vào giỏ hàng";
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

//    @PostMapping("/cart/update/{productId}/{quantity}")
//    @ResponseBody
//    public String updateProductByUser(@PathVariable("productId") String productId,
//                                    @PathVariable("quantity") String quantity, HttpServletRequest request){
//        try{
//            User user = getAuthenticatedUser(request);
//            Float subTotal = shoppingCartService.updateSubTotal(user.getId(), Integer.parseInt(productId), Integer.parseInt(quantity));
//            return String.valueOf(subTotal);
//        }catch (UserNotFoundException unf){
//            return "Bạn cần phải đăng nhập để chỉnh sửa";
//        }
//    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    @ResponseBody
    public String updateProductByUser(@PathVariable("productId") String productId,
                                      @PathVariable("quantity") String quantity, HttpServletRequest request){
        try{
            User user = getAuthenticatedUser(request);
            shoppingCartService.updateSubTotal(user.getId(), Integer.parseInt(productId), Integer.parseInt(quantity));
//            return String.valueOf(subTotal);
            return "success";
        }catch (UserNotFoundException unf){
            return "Bạn cần phải đăng nhập để chỉnh sửa";
        }
    }

    @PostMapping("/cartAnonymous/update/{productId}/{quantity}")
    @ResponseBody
    public String updateCartAnonymous(@PathVariable("productId") String productId,
                                      @PathVariable("quantity") String quantity, HttpServletRequest request) throws ProductNotFoundException {
            Map<ProductDto, Integer> cartSessions = (Map<ProductDto, Integer>) request.getSession().getAttribute("CARTS_SESSION");
            Product product = productService.getProduct(Integer.parseInt(productId));
            ProductDto productDto = productService.convertToProductDto(product);

            cartSessions.put(productDto, Integer.parseInt(quantity));
            request.getSession().setAttribute("CARTS_SESSION", cartSessions);

            return "success";
    }

    @PostMapping("/cartAnonymous/remove/{productId}")
    @ResponseBody
    public String removeProductInCartAnonymous(@PathVariable("productId") String productId, HttpServletRequest request){
        Map<ProductDto, Integer> cartsSession = (Map<ProductDto, Integer>) request.getSession().getAttribute("CARTS_SESSION");
        ProductDto productDto = new ProductDto(Integer.parseInt(productId));
        cartsSession.remove(productDto);

        request.getSession().setAttribute("CARTS_SESSION", cartsSession);
        return "success";
    }

    @PostMapping("/cart/updatebeforcheckout/{productId}/{quantity}")
    public void updateBeforeCheckout(@PathVariable("productId") String productId,
                                     @PathVariable("quantity") String quantity, HttpServletRequest request){
        try{
            User user = getAuthenticatedUser(request);
            shoppingCartService.updateSubTotal(user.getId(), Integer.parseInt(productId), Integer.parseInt(quantity));
        }catch (UserNotFoundException unf){
        }
    }

    @GetMapping("/prepareCheckout")
    @ResponseBody
    public String prepareCheckout(@RequestParam("productId") String productId, @RequestParam("productQuantity") String productQuantity,
                                  @RequestParam("checkIns") String checkIns, HttpServletRequest request){
        try{
            User user = getAuthenticatedUser(request);
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
            else
                shoppingCartService.updateSubTotal(user.getId(), Integer.parseInt(productId), quantityVal);
            return "";
        }catch (UserNotFoundException unf){
            return "Bạn cần đăng nhập để thanh toán";
        }
    }



    @GetMapping("/checkout")
    @ResponseBody
    public String checkoutCart(HttpServletRequest request) throws ProductNotFoundException {
        try{
            User user = getAuthenticatedUser(request);
//            if(user.getVerificationCodeCheckout() != null || user.getVerificationCodeCheckout() == "")
//                return "Bạn vẫn còn đơn hàng đang chờ thanh toán";
            int cartId = shoppingCartService.checkOutCart(user.getId());
            sendEmailVerityCheckOut(request, user, cartId);
        }catch (UserNotFoundException unf){
            return "Bạn cần phải đăng nhập để thanh toán";
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Bạn đã đặt hàng thành công\nKiểm tra email để xem thông tin chi tiết đơn hàng";
    }

    @GetMapping("/prepareCheckoutAnonymous")
    @ResponseBody
    public String prepareCheckoutAnonymous(@RequestParam("productId") String productId, @RequestParam("productQuantity") String productQuantity,
                                  @RequestParam("checkIns") String checkIns, HttpServletRequest request){
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

    @GetMapping("/fillInformation")
    public String fillInformation(Model model){
        AnonymousForm anonymousForm = new AnonymousForm();
        model.addAttribute("anonymousForm", anonymousForm);

        return "cart/fill-information";
    }

    @PostMapping("/fillInformation")
    public String CompleteFillInformation(@Validated @ModelAttribute("anonymousForm") AnonymousForm anonymousForm,
                                          BindingResult bindingResult, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        if(bindingResult.hasErrors())
            return "cart/fill-information";
        Map<ProductDto, Integer> cartSessions = (Map<ProductDto, Integer>) request.getSession().getAttribute("CARTS_SESSION");
        if(cartSessions == null || cartSessions.size() == 0)
            return "redirect:/cartAnonymous";
//        if(anonymousForm.isSendMail())
//            sendEmailAnonymous(anonymousForm, request);

        return "redirect:/checkoutAnonymous";
    }

//    public void sendEmailAnonymous(AnonymousForm anonymousForm, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
//        Map<ProductDto, Integer> cartsSession = (Map<ProductDto, Integer>) request.getSession().getAttribute("CARTS_SESSION");
//
////        String toAddress = anonymousForm.getEmail();
//        String subject = "Thông Tin Đơn Hàng";
//        String first_content = "<strong>Công ty [[company]]</strong> <br><br>" +
//                "Woo hoo! Đơn hàng của bạn đã được đặt thành công. Chi tiết đơn hàng bạn có thể theo dõi dưới đây\n" +
//                "<br>" +
//                "TÓM TẮT ĐƠN HÀNG: <br>" +
//                "<br>" +
//                "Đơn hàng: PO#[[number]]<br>" +
//                "Ngày đặt: [[date]]<br>" +
//                "Tổng tiền: [[price]]<br>" +
//                "<br>" +
//                "ĐỊA CHỈ GIAO HÀNG: [[address]]<br>" +
//                "<br>" +
//                "DANH SÁCH CÁC ĐƠN HÀNG: <br>";
//
//        String siteURL = request.getRequestURL().toString();
//        siteURL = siteURL.replace(request.getServletPath(), "");
//
//
//        String second_content = "";
//        Float totalMoney = 0f;
//        for(Map.Entry<ProductDto, Integer> item: cartsSession.entrySet()) {
//            second_content += "<b>Sản phẩm: </b>" + "<a href=" + siteURL + "/p/" + item.getKey().getId() + ">"
//                    + item.getKey().getName() + "</a>";
//            second_content += " <b>Số lượng: </b>" + item.getValue();
//            second_content += " <b>Thành tiền: </b>" + Math.round(item.getKey().getSubTotal()) + "<br>";
//
//            totalMoney += item.getKey().getSubTotal();
//        }
//
//            String third_content = "<br>" +
//                    "Cảm ơn bạn đã tin tưởng và mua hàng của công ty. Nếu bạn có bất kỳ câu hỏi nào, liên hệ với chúng tôi tại [[contact]] hoặc gọi trực tiếp theo số điện thoại [[phone]]\n" +
//                    "<br>" +
//                    "Thân ái,<br>" +
//                    "[[company]]";
//
//            String content = first_content + second_content + third_content;
//            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
//            LocalDateTime now = LocalDateTime.now();
//
//
//            content = content.replace("[[company]]", "FakeBook");
//            content = content.replace("[[date]]", dtf.format(now));
//            content = content.replace("[[price]]",Math.round(totalMoney) +"đ");
//            content = content.replace("[[address]]", anonymousForm.getAddress());
//            content = content.replace("[[purchase]]", siteURL+"/purchase");
//            content = content.replace("[[contact]]", siteURL+"/contact");
//            content = content.replace("[[phone]]", "09878889976");
//
//            MimeMessage message = emailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
//
//            helper.setFrom("thaixuan.thainguyen@gmail.com", "Van Luc Nguyen");
////            helper.setTo(toAddress);
//            helper.setSubject(subject);
//            helper.setText(content, true);
//
//            emailSender.send(message);
//    }

    @GetMapping("/checkoutAnonymous")
    public String checkoutCartAnonymous(HttpServletRequest request) throws ProductNotFoundException {
        Map<ProductDto, Integer> cartsSession = (Map<ProductDto, Integer>) request.getSession().getAttribute("CARTS_SESSION");
        shoppingCartService.checkoutCartAnonymous(cartsSession);

        request.getSession().invalidate();
        return "redirect:/checkoutAnonymousSuccess";
    }

    @GetMapping("/checkoutAnonymousSuccess")
    public String checkoutAnonymousSuccess(){
        return "cart/checkout-anonymous";
    }

    @GetMapping("/checkoutRequest")
    public String checkoutRequest(){
        return "cart/checkout-request";
    }

    public void sendEmailVerityCheckOut(HttpServletRequest request, User user, int cartId) throws MessagingException, UnsupportedEncodingException {
        CarDto cart = shoppingCartService.getCartDtoById(cartId);
        List<CartAndProductDto> cartAndProductList = shoppingCartService.getCartAndProductsDetail(cartId);

        String toAddress = user.getEmail();
        String subject = "Thông Tin Đơn Hàng PO"+cartId;
        String first_content = "<strong>Công ty [[company]]</strong> <br><br>" +
                "Woo hoo! Đơn hàng của bạn đã được đặt thành công. Chi tiết đơn hàng bạn có thể theo dõi dưới đây\n" +
                "<br>" +
                "TÓM TẮT ĐƠN HÀNG: <br>" +
                "<br>" +
                "Đơn hàng: PO#[[number]]<br>" +
                "Ngày đặt: [[date]]<br>" +
                "Tổng tiền: [[price]]<br>" +
                "<br>" +
                "ĐỊA CHỈ GIAO HÀNG: [[address]]<br>" +
                "<br>" +
                "DANH SÁCH CÁC ĐƠN HÀNG: <br>";

        int numberOfProduct = cartAndProductList.size();

        String siteURL = request.getRequestURL().toString();
        siteURL = siteURL.replace(request.getServletPath(), "");

        String second_content = "";
        for(int i =0; i<numberOfProduct; i++){
            second_content += "<b>Sản phẩm: </b>"+"<a href=" + siteURL+ "/p/" + cartAndProductList.get(i).getProduct().getId() + ">"
                    + cartAndProductList.get(i).getProduct().getName()+"</a>";
            second_content += " <b>Số lượng: </b>" + cartAndProductList.get(i).getQuantity();
            second_content += " <b>Thành tiền: </b>"+ (int)cartAndProductList.get(i).getSubTotal() +"<br>";

        }

        String third_content = "<b>LỊCH SỬ MUA HÀNG CỦA BẠN</b> [[purchase]]<br>" +
                "<br>" +
                "Cảm ơn bạn đã tin tưởng và mua hàng của công ty. Nếu bạn có bất kỳ câu hỏi nào, liên hệ với chúng tôi tại [[contact]] hoặc gọi trực tiếp theo số điện thoại [[phone]]\n" +
                "<br>" +
                "Thân ái,<br>" +
                "[[company]]";

        String content = first_content + second_content + third_content;

        content = content.replace("[[company]]", "FakeBook");
        content = content.replace("[[number]]", String.valueOf(cartId));
        content = content.replace("[[date]]", String.valueOf((cart.getCheckoutDate())));
        content = content.replace("[[price]]",Math.round(cart.getTotalMoney()) +"đ");
        content = content.replace("[[address]]", String.valueOf(user.getAddress()));
        content = content.replace("[[purchase]]", siteURL+"/purchase");
        content = content.replace("[[contact]]", siteURL+"/contact");
        content = content.replace("[[phone]]", "09878889976");

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setFrom("thaixuan.thainguyen@gmail.com", "Van Luc Nguyen");
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);

        emailSender.send(message);
    }

//    @GetMapping("/verifyCheckout")
//    public String verifyEmailCheckOut(@RequestParam("code") String code, Model model){
//        CheckoutType checkVar = shoppingCartService.verifyCheckOut(code);
//        if(checkVar == CheckoutType.SUCCESS){
//            shoppingCartService.verifyCheckOutSuccess(code);
//            return "cart/checkout-success";
//        }
//        String param = "";
//        if(checkVar == CheckoutType.USER_NOT_FOUND)
//            param = "USER_NOT_FOUND";
//        if(checkVar == CheckoutType.PRODUCT_NOT_FOUND)
//            param = "PRODUCT_NOT_FOUND";
//        if(checkVar == CheckoutType.EXCEED_QUANTITY)
//            param = "EXCEED_QUANTITY";
//
//        model.addAttribute("param", param);
//        return "cart/checkout-failed";
//    }
//    @GetMapping("/failedURL")
//    public String failedUrl(){
//        return "cart/checkout-failed";
//    }


    @GetMapping("/purchase")
    public String firstPagePurchaseOrder(HttpServletRequest request, Model model){

        return viewPurchaseOrder(model, request, "1", "id", "desc" );
    }

    @GetMapping("/purchase/page/{pageNum}")
    public String viewPurchaseOrder(Model model, HttpServletRequest request,
                                    @PathVariable("pageNum") String pageNum, String sortField,
                                    String sortDir){
       try{
           int currentPage = Integer.parseInt(pageNum);
           User user = getAuthenticatedUser(request);
           Boolean checkNullPurchaseOrder = true;


           Page<CarDto> page = shoppingCartService.listCartCheckout(user.getId(), currentPage, sortField, sortDir);
           List<CarDto> listCartPurchase = page.getContent();

           if(listCartPurchase == null){
               model.addAttribute("checkNullPurchaseOrder", checkNullPurchaseOrder);
               return "purchase_order";
           }



           checkNullPurchaseOrder = false;
           model.addAttribute("totalPages", page.getTotalPages());
           model.addAttribute("totalItems", page.getTotalElements());
           model.addAttribute("currentPage", currentPage);
           model.addAttribute("listCartPurchase", listCartPurchase);
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

    @GetMapping("/purchase/order/{cartId}")
    public String viewDetailCart(Model model, HttpServletRequest request, @PathVariable("cartId") String cartId){
        try{
            User user = getAuthenticatedUser(request);
            UserCartDto userCartDto = shoppingCartService.fakeUserCart(user, Integer.parseInt(cartId));
            List<CartAndProductDto> cartAndProductDtoList = shoppingCartService.getCartAndProductsDetail(Integer.parseInt(cartId));

            model.addAttribute("userCartDto", userCartDto);
            model.addAttribute("cartAndProductDtoList", cartAndProductDtoList);
            return "cart_details";
        }catch(UserNotFoundException ex){
            return "BẠn cần phải đăng nhập";
        }
    }
    public Boolean checkProductFound(int productId) {
       return productService.checkProductIsDelete(productId);
    }
}
