package com.example.login.service.impl;

import com.example.login.error.ProductNotFoundException;
import com.example.login.error.ShoppingCartException;
import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.model.Product;
import com.example.login.model.User;
import com.example.login.repository.CartProductRepository;
import com.example.login.repository.CartRepository;
import com.example.login.repository.ProductRepository;
import com.example.login.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart getCartByUser(Integer userId) {
        return cartRepository.findCartNotCheckoutByUser(userId);
    }

    @Override
    public List<CartAndProduct> listProductByUserCart(Integer cartId) {
        return cartProductRepository.listProductByUserCart(cartId);
    }

    @Override
    public CartAndProduct findProductByCart(Integer cartId, Integer productId) {
        return cartProductRepository.findProductByCart(cartId, productId);
    }

    @Override
    public Integer updateQuantity(Integer userId, Integer productId, Integer quantity) {
        Product product = new Product(productId);
        Integer updateQuantity = quantity;
        Cart cart = cartRepository.findCartNotCheckoutByUser(userId);
        if(cart == null){
            User user = new User(userId);
            cart = new Cart();
            cart.setUser(user);
            cart.setStatusPayment("NO");
            cartRepository.save(cart);
        }

        CartAndProduct cartProduct = cartProductRepository.findProductByCart(cart.getId(), productId);
        if (cartProduct != null) {
            updateQuantity += cartProduct.getQuantity();
        }else{
            cartProduct = new CartAndProduct();
            cartProduct.setProduct(product);
            cartProduct.setCart(cart);
        }
        cartProduct.setQuantity(updateQuantity);

        cartProductRepository.save(cartProduct);
        return updateQuantity;
    }

    @Override
    public String removeProductFromCart(int userId, int productId) {
        Cart cart = cartRepository.findCartNotCheckoutByUser(userId);
        CartAndProduct cartProduct = cartProductRepository.findProductByCart(cart.getId(), productId);
        if(cartProduct == null)
            return "Xóa không thành công! Sản phẩm không tồn tại hoặc không có trong giỏ hàng!";
        else{
            cartProductRepository.removeProductByCart(cart.getId(), productId);
            return "Xóa thành công";
        }

    }

    @Override
    public Float updateSubTotal(int userId, int productId, int quantity) {
        Cart cart = cartRepository.findCartNotCheckoutByUser(userId);
        CartAndProduct cartProduct = cartProductRepository.findProductByCart(cart.getId(), productId);
        cartProduct.setQuantity(quantity);
        cartProductRepository.save(cartProduct);
        return cartProduct.getProduct().getDiscountPrice() * quantity;
    }

    @Override
    public void checkOutCart(int userId) throws ProductNotFoundException {
        Cart cart = cartRepository.findCartNotCheckoutByUser(userId);
        List<CartAndProduct> listProductByUser = listProductByUserCart(cart.getId());
        for (CartAndProduct cp : listProductByUser){
            cp.setCheckoutDate(new Date());
            Product product = productRepository.findById(cp.getProduct().getId())
                    .orElseThrow(() -> new ProductNotFoundException("xx"));
            cp.setSubTotal(product.getDiscountPrice() * cp.getQuantity());
            Integer newQuantity = product.getQuantity() - cp.getQuantity();
            product.setQuantity(newQuantity);
            cartProductRepository.save(cp);
            productRepository.save(product);
        }
        cartRepository.checkOutCart(userId);
    }

    @Override
    public List<CartAndProduct> listProductPurchase(int userId) {
        List<Cart> listCartPurchase = cartRepository.listCartPurchase(userId);
        List<CartAndProduct> listProductPurchase = new ArrayList<>();
        for(Cart cart : listCartPurchase){
            List<CartAndProduct> listProductByCart = cartProductRepository.listProductByUserCart(cart.getId());
            listProductPurchase.addAll(listProductByCart);
        }
        return listProductPurchase;
    }

    @Override
    public int getQuantityProductInCart(int userId, int productId) {
        Cart cart = cartRepository.findCartNotCheckoutByUser(userId);

        if(cart == null){
            User user = new User(userId);
            cart = new Cart();
            cart.setUser(user);
            cart.setStatusPayment("NO");
            cartRepository.save(cart);
        }
        CartAndProduct productInCart = cartProductRepository.findProductByCart(cart.getId(), productId);
        if(productInCart == null)
            return 0;
        else
            return productInCart.getQuantity();
    }
}
