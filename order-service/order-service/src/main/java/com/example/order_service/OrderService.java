package com.example.order_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Order createOrder(Order order) {
        try {
            // Check inventory
            String inventoryUrl = "http://inventory-service/api/inventory/check?productId=" + order.getProductId() + "&quantity=" + order.getQuantity();
            Boolean isAvailable = restTemplate.getForObject(inventoryUrl, Boolean.class);

            if (isAvailable != null && isAvailable) {
                order.setCreatedDate(new Date());
                order.setStatus("CREATED");
                Order savedOrder = orderRepository.save(order);

                // Update inventory
                String updateInventoryUrl = "http://inventory-service/api/inventory/update";
                restTemplate.postForObject(updateInventoryUrl, order, Void.class);

                return savedOrder;
            } else {
                throw new RuntimeException("Product not available in inventory");
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error communicating with inventory service: " + e.getMessage());
        }
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}
