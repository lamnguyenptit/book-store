package com.example.login.service.impl;

import com.example.login.error.ProductNotFoundException;
import com.example.login.model.Category;
import com.example.login.model.Product;
import com.example.login.model.Publisher;
import com.example.login.model.dto.CategoryDto;
import com.example.login.model.dto.ProductDto;
import com.example.login.model.dto.PublisherDto;
import com.example.login.repository.CategoryRepository;
import com.example.login.repository.ProductRepository;
import com.example.login.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    public static final int PRODUCT_SEARCH = 2;

    @Autowired
    private ProductRepository repo;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Product> listProductByCategory(Integer catId) {
        Pageable pageable = PageRequest.of(0, 10);
        List<Integer> listAllProductIdByCategory = repo.getAllIdProductCategories(catId);
        List<Product> listAllProduct = new ArrayList<>();
        for (Integer productId :
                listAllProductIdByCategory) {
            Product product = repo.findById(productId).get();
            listAllProduct.add(product);
        }
        return listAllProduct;
    }

    @Override
    public Product getProduct(String id) throws ProductNotFoundException {
        Optional<Product> productOptional = repo.findById(Integer.parseInt(id));
        return productOptional
                .orElseThrow(() -> new ProductNotFoundException("không thể tìm thấy sản phẩm"));
    }

    @Override
    public Page<Product> searchProduct(String keyword, int currentPage) {
        Pageable pageable = PageRequest.of(currentPage - 1,PRODUCT_SEARCH);
        return repo.findAllProduct(keyword, pageable);
    }

    @Override
    public List<ProductDto> findAllProduct(){
        return convertToDtos(repo.findAll());
    }

    @Override
    public void createProduct(ProductDto productDto) {
        Category category = new Category();
        BeanUtils.copyProperties(productDto.getCategory(), category);
        Publisher publisher = new Publisher();
        BeanUtils.copyProperties(productDto.getPublisher(), publisher);
        Product product = new Product(productDto.getName(), productDto.getCost(), productDto.getCreateTime(), productDto.getDiscountPercent(), productDto.isEnabled(), productDto.getDescription(), productDto.getImage(), productDto.isInStock(), productDto.getQuantity(), productDto.getPrice(), category, publisher);
        repo.save(product);
    }

    public ProductDto convertToDto(Product product){
        if (product == null)
            return null;
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(product, productDto);

        CategoryDto categoryDto = new CategoryDto();
        //BeanUtils.copyProperties(product.getCategory(), categoryDto);
        productDto.setCategory(categoryDto);

        PublisherDto publisherDto = new PublisherDto();
        BeanUtils.copyProperties(product.getPublisher(), publisherDto);
        productDto.setPublisher(publisherDto);
        return productDto;
    }

    public List<ProductDto> convertToDtos(List<Product> products){
        if (products == null)
            return null;
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public Integer getQuantityProduct(Integer productId) {
        return repo.getQuantityProduct(productId);
    }

    @Override
    public List<Product> listProductSameCategory(Integer productId) {
        List<Product> getSomeProduct = new ArrayList<>();
        Product product = repo.findById(productId).get();
        Set<Category> listAllCategory = product.getCategories();
        for (Category category :
                listAllCategory) {
            List<Product> listProductByCategory = category.getProductSet();
            listProductByCategory.removeAll(Arrays.asList(product));
            List<Product> listSubList = (listProductByCategory.size() <=2) ? listProductByCategory.subList(0,listProductByCategory.size()) : listProductByCategory.subList(0,2);
            getSomeProduct.addAll(listSubList);
        }
        return  getSomeProduct;
    }

    @Override
    public List<Product> listProductSameMoney(int productId) {
        Product product = repo.findById(productId).get();
        double money = product.getDiscountPrice();
        List<Product> listProductSameMoney = repo.listProductSameMoney(money);
        listProductSameMoney.removeAll(Arrays.asList(product));
        return listProductSameMoney;
    }
}