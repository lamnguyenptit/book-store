package com.example.login.service.impl;

import com.example.login.error.ProductNotFoundException;
import com.example.login.error.ShoppingCartException;
import com.example.login.model.Cart;
import com.example.login.model.CartAndProduct;
import com.example.login.model.Product;
import com.example.login.model.User;
import com.example.login.model.dto.CartAndProductDto;
import com.example.login.model.dto.CartDto;
import com.example.login.model.dto.ProductDto;
import com.example.login.model.dto.UserDto;
import com.example.login.model.*;
import com.example.login.model.dto.CartAndProductDto;
import com.example.login.model.dto.CartDTO;
import com.example.login.model.dto.ProductDto;
import com.example.login.model.dto.UserCartDto;
import com.example.login.repository.CartProductRepository;
import com.example.login.repository.CartRepository;
import com.example.login.repository.ProductRepository;
import com.example.login.repository.UserRepository;
import com.example.login.service.CartAndProductService;
import com.example.login.service.ShoppingCartService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    public final static int PRODUCT_PER_PAGE = 2;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private CartAndProductService cartAndProductService;

    @Override
    public Cart getCartByUser(Integer userId) {
        return cartRepository.findCartNotCheckoutByUser(userId);
    }

    @Override
    public Page<CartAndProduct> listProductByUserCart(Integer cartId, int currentPage, String fieldName, String sortDir) {
        Sort sort = Sort.by(fieldName);
        sort = sortDir.equals("asc") ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(currentPage - 1, PRODUCT_PER_PAGE, sort);
        List<CartAndProduct> content =  cartProductRepository.listProductByUserCart(cartId);

        Comparator<CartAndProduct> compareByField = new Comparator<CartAndProduct>() {
            @Override
            public int compare(CartAndProduct o1, CartAndProduct o2) {
                switch (fieldName){
                    case "id": return o1.getProduct().getId().compareTo(o2.getProduct().getId());
                    case "quantity": return o1.getQuantity().compareTo(o2.getQuantity());
                    case "subTotal": return o1.getSubTotal().compareTo(o2.getSubTotal());
                }
                return 0;
            }
        };

        if(sortDir.equals("asc"))
            Collections.sort(content, compareByField);
        else
            Collections.sort(content, compareByField.reversed());


         int start = (int) pageable.getOffset();
         int end = start + PRODUCT_PER_PAGE ; // co the su dung Math.min()
        if(end > content.size())
            end = content.size();
        return new PageImpl<>(content.subList(start, end), pageable, content.size());
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

//    @Override
//    public Float updateSubTotal(int userId, int productId, int quantity) {
//        Cart cart = cartRepository.findCartNotCheckoutByUser(userId);
//        CartAndProduct cartProduct = cartProductRepository.findProductByCart(cart.getId(), productId);
//        cartProduct.setQuantity(quantity);
//        cartProductRepository.save(cartProduct);
//        return cartProduct.getProduct().getDiscountPrice() * quantity;
//    }

    @Override
    public void updateSubTotal(int userId, int productId, int quantity) {
        Cart cart = cartRepository.findCartNotCheckoutByUser(userId);
        CartAndProduct cartProduct = cartProductRepository.findProductByCart(cart.getId(), productId);
        cartProduct.setQuantity(quantity);
        cartProductRepository.save(cartProduct);
    }

    @Override
    public int checkOutCart(int userId) throws ProductNotFoundException {
//        User user = userRepository.getById(userId);
//        String randomCode = RandomString.make(64);
//        user.setVerificationCodeCheckout(randomCode);

        Cart cart = cartRepository.findCartNotCheckoutByUser(userId);
        List<CartAndProduct> listProductByUser = listProductByUserCart(cart.getId());
        for (CartAndProduct cp : listProductByUser){
            cp.setCheckoutDate(new Date());
            Product product = productRepository.findById(cp.getProduct().getId())
                    .orElseThrow(() -> new ProductNotFoundException("Sản phẩm không tồn tại hoặc đã bị xóa"));
            cp.setSubTotal(product.getDiscountPrice() * cp.getQuantity());
            Integer newQuantity = product.getQuantity() - cp.getQuantity();
            product.setQuantity(newQuantity);
            cartProductRepository.save(cp);
            productRepository.save(product);
        }
        cartRepository.checkOutCart(userId);
        return cart.getId();
    }

    @Override
    public Page<CartDTO> listCartCheckout(int userId, int pageNum, String sortField, String sortDir) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);

        List<Cart> listCartPurchase = cartRepository.listCartPurchase(userId);
        List<CartDTO> cartDTOList = listCartPurchase.stream().map(this::convertToCartDto).collect(Collectors.toList());
        Comparator<CartDTO> compareByField = new Comparator<CartDTO>() {
            @Override
            public int compare(CartDTO o1, CartDTO o2) {
                switch (sortField){
                    case "id": return o1.getId().compareTo(o2.getId());
                    case "checkoutDate": return o1.getCheckoutDate().compareTo(o2.getCheckoutDate());
                    case "totalMoney": return o1.getTotalMoney().compareTo(o2.getTotalMoney());
                }
                return 0;
            }
        };

        if(sortDir.equals("asc"))
            Collections.sort(cartDTOList, compareByField);
        else
            Collections.sort(cartDTOList, compareByField.reversed());

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), cartDTOList.size());

        Page<CartDTO> listPage = new PageImpl<>(cartDTOList.subList(start,end), pageable, cartDTOList.size());

        return listPage;
    }

    public CartDTO convertToCartDto(Cart cart){
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setCartAndProductDtoList(convertToCartAndProductDtoList(cart.getCartAssoc()));
        return cartDTO;
    }

    public List<CartAndProductDto> convertToCartAndProductDtoList(List<CartAndProduct> cartAndProductList){
        if(cartAndProductList == null)
            return null;
        return cartAndProductList.stream().map(this::convertToCartAndProductDto).collect(Collectors.toList());
    }

    public CartAndProductDto convertToCartAndProductDto(CartAndProduct item){
        CartAndProductDto dto = new CartAndProductDto();
        dto.setProductDto(convertToProductDto(item.getProduct()));
        dto.setCheckoutDate(item.getCheckoutDate());
        dto.setSubTotal(item.getSubTotal());
        dto.setQuantity(item.getQuantity());
        return dto;
    }

    public ProductDto convertToProductDto(Product product){
        if(product == null)
            return null;
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setImage(product.getImage());
        return productDto;
    }

    @Override
    public Page<CartAndProduct> listProductPurchase(int userId, int pageNum, String sortField, String sortDir) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);

        List<Cart> listCartPurchase = cartRepository.listCartPurchase(userId);
        List<CartAndProduct> listProductPurchase = new ArrayList<>();
        for(Cart cart : listCartPurchase){
            List<CartAndProduct> listProductByCart = cartProductRepository.listProductByUserCart(cart.getId());
            listProductPurchase.addAll(listProductByCart);
        }


        Comparator<CartAndProduct> compareByField = new Comparator<CartAndProduct>() {
            @Override
            public int compare(CartAndProduct o1, CartAndProduct o2) {
                switch (sortField){
                    case "id": return o1.getCart().getId().compareTo(o2.getCart().getId());
                    case "quantity": return o1.getQuantity().compareTo(o2.getQuantity());
                    case "checkoutDate": return o1.getCheckoutDate().compareTo(o2.getCheckoutDate());
                }
                return 0;
            }
        };

        if(sortDir.equals("asc"))
            Collections.sort(listProductPurchase, compareByField);
        else
            Collections.sort(listProductPurchase, compareByField.reversed());

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), listProductPurchase.size());
        Page<CartAndProduct> listPage = new PageImpl<>(listProductPurchase.subList(start,end), pageable, listProductPurchase.size());

        return listPage;
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

    @Override
    public List<CartDto> findAll() {
        return convertToDtos(cartRepository.findAll());
    }

    @Override
    public CartDto findById(int id) {
        return convertToDto(cartRepository.findById(id).orElse(null));
    }

    @Override
    public void updateOrder(CartDto cartDto) {
        Cart cart = cartRepository.findById(cartDto.getId()).orElse(null);
        if (cart == null)
            return;
        cart.setName(cartDto.getName());
        cart.setAddress(cartDto.getAddress());
        cart.setPhone(cartDto.getPhone());
        if (cartDto.getCartAndProducts().isEmpty() || cartDto.getCartAndProducts() == null)
            cart.setCartAssoc(null);
        else {
            List<CartAndProduct> cartAndProducts = new ArrayList<>();
            cartDto.getCartAndProducts().forEach(cartAndProductDto -> {
                CartAndProduct cartAndProduct = cartAndProductService.findById(cartAndProductDto.getId());
                if (cartAndProduct == null)
                    return;
                cartAndProduct.setQuantity(cartAndProductDto.getQuantity());
                cartAndProducts.add(cartAndProduct);
            });
            cart.getCartAssoc().clear();
            cartAndProducts.forEach(cart.getCartAssoc()::add);
        }
        cartRepository.saveAndFlush(cart);
    }

    @Override
    public void deleteById(int id) {
        cartRepository.deleteById(id);
    }

    private CartDto convertToDto(Cart cart){
        if (cart == null)
            return null;
        CartDto cartDto = new CartDto();
        BeanUtils.copyProperties(cart, cartDto);

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(cart.getUser(), userDto);
        cartDto.setUser(userDto);

        List<CartAndProductDto> cartAndProductDtos = new ArrayList<>();
        cart.getCartAssoc().forEach(cartAndProduct -> {
            CartAndProductDto cartAndProductDto = new CartAndProductDto();
            BeanUtils.copyProperties(cartAndProduct, cartAndProductDto);
            ProductDto productDto = new ProductDto();
            BeanUtils.copyProperties(cartAndProduct.getProduct(), productDto);
            cartAndProductDto.setProduct(productDto);
            cartAndProductDtos.add(cartAndProductDto);
        });
        cartDto.setCartAndProducts(cartAndProductDtos);
        return cartDto;
    }

    private List<CartDto> convertToDtos(List<Cart> carts) {
        if (carts == null)
            return null;
        return carts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserCartDto fakeUserCart(User user, int cartId) {
        UserCartDto userCartDto = new UserCartDto();
        userCartDto.setName(user.getName());
        userCartDto.setAddress(user.getAddress());
        userCartDto.setPhone(user.getPhone());

        Cart cart = cartRepository.getById(cartId);
        userCartDto.setCartId(cartId);
        CartDTO cartDTO = convertToCartDto(cart);
        userCartDto.setTotalMoney(cartDTO.getTotalMoney());
        userCartDto.setCheckoutDate(cartDTO.getCheckoutDate());
        userCartDto.setPhone(user.getPhone());
        userCartDto.setAddress(user.getAddress());
        return userCartDto;
    }

    @Override
    public List<CartAndProductDto> getCartAndProductsDetail(int cartId) {
        List<CartAndProduct> cartAndProductList = cartProductRepository.listProductByUserCart(cartId);

        return convertToCartAndProductDtoList(cartAndProductList);
    }

//    @Override
//    public CheckoutType verifyCheckOut(String code) {
//        User user = userRepository.findByVerificationCodeCheckout(code);
//        if(user == null || !user.isEnabled() || user.isLocked())
//            return CheckoutType.USER_NOT_FOUND;
//        Cart cart = cartRepository.findCartDelayByUser(user.getId());
//        if(cart == null)
//            return CheckoutType.CART_NOT_FOUND;
//        for (CartAndProduct cap :
//                cart.getCartAssoc()) {
//            Product product = productRepository.getById(cap.getProductId());
//            if(product == null || !product.isInStock() || !product.isEnabled())
//                return CheckoutType.PRODUCT_NOT_FOUND;
//            if(cap.getQuantity() > product.getQuantity())
//                return CheckoutType.EXCEED_QUANTITY;
//        }
//        return CheckoutType.SUCCESS;
//    }
//
//    @Override
//    public void verifyCheckOutSuccess(String code) {
//        User user = userRepository.findByVerificationCodeCheckout(code);
//        user.setVerificationCodeCheckout("");
//        userRepository.save(user);
//        cartRepository.updateSuccessCheckoutCart(user.getId());
//    }

    @Override
    public Cart getCartDelayByUser(int userId) {
        return cartRepository.findCartDelayByUser(userId);
    }

    @Override
    public CartDTO getCartDtoById(int cartId) {
        return convertToCartDto( cartRepository.getById(cartId));
    }

    @Override
    public void checkoutCartAnonymous(Map<ProductDto, Integer> cartsSession) throws ProductNotFoundException {
        Cart cart = new Cart();
        cart.setStatusPayment("ANONYMOUS");

        for(Map.Entry<ProductDto, Integer> item : cartsSession.entrySet()){
            Product product = productRepository.findById(item.getKey().getId()).
                    orElseThrow(() -> new ProductNotFoundException("Sản phẩm không tồn tại hoặc đã bị xóa"));
            CartAndProduct cap = new CartAndProduct();
            cap.setCart(cart);
            cap.setProduct(product);
            cap.setQuantity(item.getValue());
            cap.setCheckoutDate(new Date());

            Integer newQuantity = product.getQuantity() - cap.getQuantity();
            product.setQuantity(newQuantity);
            cartProductRepository.save(cap);
            productRepository.save(product);
        }
        cartRepository.save(cart);

    }
}
