package com.example.login.service;

import com.example.login.error.ProductNotFoundException;
import com.example.login.error.ShoppingCartException;
import com.example.login.model.*;
import com.example.login.model.dto.CartAndProductDto;
import com.example.login.model.dto.CartDTO;
import com.example.login.model.dto.ProductDto;
import com.example.login.model.dto.UserCartDto;
import org.springframework.data.domain.Page;
import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.model.Product;
import com.example.login.model.dto.CartDto;

import java.util.List;
import java.util.Map;

public interface ShoppingCartService {

    public Cart getCartByUser(Integer userId);

    public Page<CartAndProduct> listProductByUserCart(Integer cartId, int currentPage, String fieldName, String sortDir);
    public List<CartAndProduct> listProductByUserCart(Integer cartId);

    public Integer updateQuantity(Integer userId, Integer productId, Integer quantity) throws ShoppingCartException;

    public CartAndProduct findProductByCart(Integer cartId, Integer productId);

    String removeProductFromCart(int userId, int productId);

    void updateSubTotal(int userId, int productId, int quantity);

    int checkOutCart(int userId) throws ProductNotFoundException;

    Page<CartAndProduct> listProductPurchase(int userId, int pageNum, String sortField, String sortDir);

    int getQuantityProductInCart(int userId, int productId);

    List<CartDto> findAll();

    CartDto findById(int id);

    void updateOrder(CartDto cartDto);

    void deleteById(int id);

    Page<CartDTO> listCartCheckout(int userId, int currentPage, String sortField, String sortDir);

    UserCartDto fakeUserCart(User user, int cartId);

    List<CartAndProductDto> getCartAndProductsDetail(int cartId);

//    CheckoutType verifyCheckOut(String code);
//
//    void verifyCheckOutSuccess(String code);

    Cart getCartDelayByUser(int userId);

    CartDTO getCartDtoById(int cartId);

    void checkoutCartAnonymous(Map<ProductDto, Integer> cartsSession) throws ProductNotFoundException;
}
