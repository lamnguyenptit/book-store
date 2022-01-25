package com.example.login.service.impl;

import com.example.login.error.ProductNotFoundException;
import com.example.login.model.Category;
import com.example.login.model.Product;
import com.example.login.model.Publisher;
import com.example.login.model.dto.CategoryDto;
import com.example.login.model.dto.ProductDto;
import com.example.login.model.dto.PublisherDto;
import com.example.login.repository.ProductRepository;
import com.example.login.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repo;

    @Override
    public Page<Product> listProductByCategory(Integer catId) {
        Pageable pageable = PageRequest.of(0, 10);
        return repo.listProductByCategory(catId, pageable);
    }

    @Override
    public Product getProduct(String id) throws ProductNotFoundException {
        Optional<Product> productOptional = repo.findById(Integer.parseInt(id));
        return productOptional
                .orElseThrow(() -> new ProductNotFoundException("không thể tìm thấy sản phẩm"));
    }

    @Override
    public Page<Product> searchProduct(String keyword) {
        Pageable pageable = PageRequest.of(0,10);
        return repo.findAllProduct(keyword, pageable);
    }

    @Override
    public Page<ProductDto> findAllProduct(Pageable pageable){
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Product> products = repo.findAll();
        List<ProductDto> list;
        if (products.size() < startItem)
            list = Collections.emptyList();
        else {
            int toIndex = Math.min(startItem + pageSize, products.size());
            list = convertToDtos(products.subList(startItem, toIndex));
        }
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), products.size());
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

    @Override
    public void deleteProduct(int id) {
        Product product = repo.getById(id);
        if (product.getImage() != null || !product.getImage().equals("")){
            String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/product-images/";
            String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/product-images/";
            try {
                Files.deleteIfExists(Paths.get(filePath1 + product.getImage()));
                Files.deleteIfExists(Paths.get(filePath2 + product.getImage()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        repo.delete(product);
    }

    @Override
    public ProductDto findById(int id) {
        return convertToDto(repo.findById(id).orElse(null));
    }

    @Override
    public Page<ProductDto> findProductsByName(Pageable pageable, String name){
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Product> products = repo.findAllByNameContainingIgnoreCase(name);
        List<ProductDto> list;
        if (products.size() < startItem)
            list = Collections.emptyList();
        else {
            int toIndex = Math.min(startItem + pageSize, products.size());
            list = convertToDtos(products.subList(startItem, toIndex));
        }
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), products.size());
    }

    @Override
    public void updateProduct(ProductDto productDto) {
        Product product = repo.findById(productDto.getId()).orElse(null);
        if (product == null)
            return;
        product.setName(productDto.getName());
        product.setEnabled(productDto.isEnabled());
        product.setInStock(productDto.isInStock());
        product.setCost(productDto.getCost());
        product.setPrice(productDto.getPrice());
        product.setDiscountPercent(productDto.getDiscountPercent());
        product.setQuantity(productDto.getQuantity());
        product.setDescription(productDto.getDescription());
        Category category = new Category();
        BeanUtils.copyProperties(productDto.getCategory(), category);
        product.setCategory(category);
        Publisher publisher = new Publisher();
        BeanUtils.copyProperties(productDto.getPublisher(), publisher);
        product.setPublisher(publisher);
        product.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        if (product.getImage() == null || product.getImage().equals(""))
            product.setImage(productDto.getImage());
        repo.saveAndFlush(product);
    }

    public ProductDto convertToDto(Product product){
        if (product == null)
            return null;
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(product, productDto, "id", "createTime", "updateTime", "productAssoc");

        CategoryDto categoryDto = new CategoryDto();
        BeanUtils.copyProperties(product.getCategory(), categoryDto);
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
}