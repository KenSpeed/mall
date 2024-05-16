package com.ken.mall.controller;

import com.ken.mall.domain.Product;
import com.ken.mall.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getProducts(@RequestParam(defaultValue = "1", required = false) int page,
                                     @RequestParam(defaultValue = "15",  required = false) int size) {
        return productService.getProducts(page, size);
    }

}
