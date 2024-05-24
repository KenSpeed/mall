package com.ken.mall.service;

import com.ken.mall.domain.Product;
import com.ken.mall.fixture.ProductTestFixture;
import com.ken.mall.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ProductServiceTest {


    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProducts() {
        Product product = ProductTestFixture.createDefaultProduct();

        List<Product> productList = Arrays.asList(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> result = productService.getAllProducts(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("12345", result.getContent().get(0).getSku());
    }

    @Test
    public void testGetProductBySku() {
        Product product = ProductTestFixture.createDefaultProduct();

        when(productRepository.findBySku("12345")).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductBySku("12345");

        assertTrue(result.isPresent());
        assertEquals("12345", result.get().getSku());
    }

    @Test
    public void testGetProductBySku_NotFound() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductBySku("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetProductsByCategory() {
        Product product = ProductTestFixture.createDefaultProduct();

        when(productRepository.findByCategory("Category")).thenReturn(Arrays.asList(product));

        List<Product> result = productService.getProductsByCategory("Category");

        assertEquals(1, result.size());
        assertEquals("Category", result.get(0).getCategory());
    }

    @Test
    public void testSearchProducts() {
        Product product = ProductTestFixture.createDefaultProduct();

        when(productRepository.findByNameContainingOrDescriptionContaining(anyString(), anyString())).thenReturn(Arrays.asList(product));

        List<Product> result = productService.searchProducts("Test");

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }
}
