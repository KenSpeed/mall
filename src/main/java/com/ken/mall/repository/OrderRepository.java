package com.ken.mall.repository;

import com.ken.mall.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndProductId(String status, Long productId);
    List<Order> findByStatus(String status);
}
