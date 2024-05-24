package com.ken.mall.controller;

import com.ken.mall.domain.Product;
import com.ken.mall.fixture.ProductTestFixture;
import com.ken.mall.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    public void testGetProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(15)); // 默认每页大小为 15
    }

    @Test
    public void testGetProductsPage2() throws Exception {
        mockMvc.perform(get("/api/products?page=2"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(15)); // 默认每页大小为 15
    }

    @Test
    public void testGetProductsWithCustomSize() throws Exception {
        mockMvc.perform(get("/api/products?page=1&size=10"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(10));
    }


    @Test
    public void testGetAllProducts() throws Exception {
        Product product = ProductTestFixture.createDefaultProduct();

        List<Product> productList = Arrays.asList(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(get("/api/products"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].sku").value("12345"));
    }

    @Test
    public void testGetProductBySku() throws Exception {
        Product product = ProductTestFixture.createDefaultProduct();

        when(productService.getProductBySku("12345")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/12345"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.sku").value("12345"));
    }

    @Test
    public void testGetProductBySku_NotFound() throws Exception {
        when(productService.getProductBySku(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/unknown"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProductsByCategory() throws Exception {
        Product product = ProductTestFixture.createDefaultProduct();

        when(productService.getProductsByCategory("Category")).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/api/products/category/Category"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].category").value("Category"));
    }

    @Test
    public void testSearchProducts() throws Exception {
        Product product = ProductTestFixture.createDefaultProduct();

        when(productService.searchProducts("Test")).thenReturn(Arrays.asList(product));

        mockMvc.perform(post("/api/products/search")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("Test"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value("Test Product"));
    }
}
