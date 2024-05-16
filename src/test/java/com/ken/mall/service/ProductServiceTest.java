package com.ken.mall.service;

import com.ken.mall.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

public class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService();
    }

    @Test
    public void testGetProducts() {
        List<Product> products = productService.getProducts(1, 15);
        assertEquals(15, products.size()); // 默认每页大小为 15
    }

    @Test
    public void testGetProductsPage2() {
        List<Product> products = productService.getProducts(2, 15);
        assertEquals(15, products.size()); // 默认每页大小为 15
    }

    @Test
    public void testGetProductsWithCustomSize() {
        List<Product> products = productService.getProducts(1, 10);
        assertEquals(10, products.size());
    }
}
