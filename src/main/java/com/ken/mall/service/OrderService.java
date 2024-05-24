package com.ken.mall.service;

import com.ken.mall.domain.Order;
import com.ken.mall.domain.Product;
import com.ken.mall.enums.OrderStatus;
import com.ken.mall.exception.OrderException;
import com.ken.mall.repository.OrderRepository;
import com.ken.mall.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrder(String sku, int quantity) throws OrderException {
        // Check for unpaid orders
        List<Order> unpaidOrders = orderRepository.findByStatus(OrderStatus.PENDING.name());
        if (!unpaidOrders.isEmpty()) {
            throw new OrderException("You have unpaid orders. Please complete payment before creating a new order.");
        }

        // Validate product availability and price
        Optional<Product> productWithOptional = productRepository.findBySku(sku);
        if (productWithOptional.isEmpty()) {
            throw new OrderException("Product not found.");
        }

        Product product = productWithOptional.get();
        if (product.getStock() < quantity) {
            throw new OrderException("Insufficient stock.");
        }

        double currentPrice = product.getPrice();
        if (currentPrice != product.getPrice()) {
            throw new OrderException("Price has changed. Please confirm the new price before placing the order.");
        }

        // Create order
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setTotalPrice(currentPrice * quantity);
        order.setStatus("pending");
        order.setOrderDate(new Date());

        // Reduce stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        return orderRepository.save(order);
    }

    public List<Order> getUnpaidOrders() {
        return orderRepository.findByStatus(OrderStatus.PENDING.name());
    }
}