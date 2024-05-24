package com.ken.mall.controller;

import com.ken.mall.domain.Order;
import com.ken.mall.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam String sku, @RequestParam int quantity) {
        try {
            Order order = orderService.createOrder(sku, quantity);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/unpaid")
    public ResponseEntity<?> getUnpaidOrders() {
        return ResponseEntity.ok(orderService.getUnpaidOrders());
    }
}