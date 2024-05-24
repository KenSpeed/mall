package com.ken.mall.fixture;

import com.ken.mall.domain.Product;

public class ProductTestFixture {

    public static Product createDefaultProduct() {
        Product product = new Product();
        product.setSku("12345");
        product.setName("Test Product");
        product.setDescription("Description");
        product.setCategory("Category");
        product.setPrice(100.0);
        product.setStock(10000);
        return product;
    }

}
