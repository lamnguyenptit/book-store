package com.example.login;

import com.example.login.model.Category;
import com.example.login.model.Product;
import com.example.login.repository.CategoryRepository;
import com.example.login.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTest {
    @Autowired
    ProductRepository repo;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    public void testFindById(){
        Integer id = 1;
        Optional<Product> product = repo.findById(id);

        assertThat(product).isNotNull();
        System.out.printf(product.get().getName());
    }

    @Test
    public void testGetQuantity(){
        Integer id = 1;
        Integer quantity = repo.getQuantityProduct(id);
        assertThat(quantity).isGreaterThan(0);
        System.out.printf(String.valueOf(quantity));
    }

    @Test
    public void testListAllIdProductByCategory(){
        Integer categoryId = 3;
        List<Integer> listIdProductByCategory = repo.getAllIdProductCategories(categoryId);
        assertThat(listIdProductByCategory).isNotNull();
        listIdProductByCategory.forEach(System.out::println);
    }

    @Test
    public void testListAllCategoryByProduct(){
        Integer productId = 1;
        Product product = repo.findById(productId).get();
        Set<Category> listAllCategory = product.getCategories();
        assertThat(listAllCategory).isNotNull();
        for (Category c:
             listAllCategory) {
            System.out.println(c.getId());
        }
    }

    @Test
    public void testListAllProductByCategory(){
        Integer categoryId = 2;
        Category category = categoryRepository.findById(categoryId).get();
        List<Product> listAllProduct = category.getProductSet();
        assertThat(listAllProduct).isNotNull();
        for (Product product :
                listAllProduct) {
            System.out.println(product.getId());
        }
    }
    
    @Test
    public void testGetAllProductSameMoney(){
        
        double money = 100000d;
        List<Product> listProduct = repo.listProductSameMoney(money);
        assertThat(listProduct).isNotNull();
        for (Product product :
                listProduct) {
            System.out.println(product.getId());
        }
    }

}
