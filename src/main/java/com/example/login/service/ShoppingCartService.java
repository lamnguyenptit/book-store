package com.example.login.service;

import com.example.login.error.ProductNotFoundException;
import com.example.login.error.ShoppingCartException;
import com.example.login.model.*;
import com.example.login.model.dto.CartAndProductDto;
import com.example.login.model.dto.ProductDto;
import com.example.login.model.dto.UserCartDto;
import org.springframework.data.domain.Page;
import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.model.dto.CarDto;

import java.util.List;
import java.util.Map;

public interface ShoppingCartService {

    public Cart getCartByUser(Integer userId);

    public Page<CartAndProduct> listProductByUserCart(Integer cartId, int currentPage, String fieldName, String sortDir) throws ProductNotFoundException;
    
    public List<CartAndProduct> listProductByUserCart(Integer cartId);

    public Integer updateQuantity(Integer userId, Integer productId, Integer quantity) throws ShoppingCartException;

    public CartAndProduct findProductByCart(Integer cartId, Integer productId);

    String removeProductFromCart(int userId, int productId);

    void updateSubTotal(int userId, int productId, int quantity);

    int checkOutCart(int userId) throws ProductNotFoundException;

    Page<CartAndProduct> listProductPurchase(int userId, int pageNum, String sortField, String sortDir);

    int getQuantityProductInCart(int userId, int productId);

    List<CarDto> findAll();

    CarDto findById(int id);

    void updateOrder(CarDto carDto);

    void deleteById(int id);

    Page<CarDto> listCartCheckout(int userId, int currentPage, String sortField, String sortDir);

    UserCartDto fakeUserCart(User user, int cartId);

    List<CartAndProductDto> getCartAndProductsDetail(int cartId);

//    CheckoutType verifyCheckOut(String code);
//
//    void verifyCheckOutSuccess(String code);

    Cart getCartDelayByUser(int userId);

    CarDto getCartDtoById(int cartId);

    void checkoutCartAnonymous(Map<ProductDto, Integer> cartsSession) throws ProductNotFoundException;

    List<CarDto> listCartDtoToExport(int userId);
}
