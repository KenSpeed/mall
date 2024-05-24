package com.ken.mall.controller;

import com.ken.mall.domain.Order;
import com.ken.mall.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderControllerTest {
    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void testCreateOrder_Success() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD123");
        order.setStatus("pending");

        when(orderService.createOrder(anyString(), anyInt())).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                       .param("sku", "12345")
                       .param("quantity", "1")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.orderNumber").value("ORD123"))
               .andExpect(jsonPath("$.status").value("pending"));
    }

    @Test
    public void testCreateOrder_InvalidInput() throws Exception {
        mockMvc.perform(post("/api/orders")
                       .param("sku", "invalid_sku")
                       .param("quantity", "0")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUnpaidOrders() throws Exception {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setOrderNumber("ORD123");
        order1.setStatus("pending");

        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderNumber("ORD456");
        order2.setStatus("pending");

        List<Order> unpaidOrders = Arrays.asList(order1, order2);

        when(orderService.getUnpaidOrders()).thenReturn(unpaidOrders);

        mockMvc.perform(get("/api/orders/unpaid"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].orderNumber").value("ORD123"))
               .andExpect(jsonPath("$[0].status").value("pending"))
               .andExpect(jsonPath("$[1].id").value(2))
               .andExpect(jsonPath("$[1].orderNumber").value("ORD456"))
               .andExpect(jsonPath("$[1].status").value("pending"));
    }
}