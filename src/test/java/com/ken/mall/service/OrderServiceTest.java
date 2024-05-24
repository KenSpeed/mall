package com.ken.mall.service;

import com.ken.mall.domain.Order;
import com.ken.mall.domain.Product;
import com.ken.mall.fixture.ProductTestFixture;
import com.ken.mall.repository.OrderRepository;
import com.ken.mall.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrder_Success() throws Exception {
        Product product = ProductTestFixture.createDefaultProduct();

        when(productRepository.findBySku("12345")).thenReturn(Optional.of(product));
        when(orderRepository.findByStatus("pending")).thenReturn(Collections.emptyList());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.createOrder("12345", 1);

        assertTrue(order.getOrderNumber() != null && !order.getOrderNumber().isEmpty());
        assertTrue(order.getStatus().equals("pending"));
        assertTrue(order.getTotalPrice() == 100.0);

        verify(productRepository, times(1)).save(any(Product.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testCreateOrder_UnpaidOrderExists() {
        when(orderRepository.findByStatus("pending")).thenReturn(Collections.singletonList(new Order()));

        Exception exception = assertThrows(Exception.class, () -> orderService.createOrder("12345", 1));
        assertTrue(exception.getMessage().contains("You have unpaid orders"));
    }

    @Test
    public void testCreateOrder_InsufficientStock() {
        Product product = new Product();
        product.setSku("12345");
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(0);

        when(productRepository.findBySku("12345")).thenReturn(Optional.of(product));
        when(orderRepository.findByStatus("pending")).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(Exception.class, () -> orderService.createOrder("12345", 1));
        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    public void testCreateOrder_PriceChanged() {
        Product product = new Product();
        product.setSku("12345");
        product.setName("Test Product");
        product.setPrice(200.0); // Price change
        product.setStock(10);

        when(productRepository.findBySku("12345")).thenReturn(Optional.of(product));
        when(orderRepository.findByStatus("pending")).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(Exception.class, () -> orderService.createOrder("12345", 1));
        assertTrue(exception.getMessage().contains("Price has changed"));
    }
}
