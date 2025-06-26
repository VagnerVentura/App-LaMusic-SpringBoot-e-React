package com.LaMusic.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LaMusic.Mappers.OrderMapper;
import com.LaMusic.dto.CheckoutRequestDTO;
import com.LaMusic.dto.OrderResponseDTO;
import com.LaMusic.entity.Order;
import com.LaMusic.services.OrderService;
import com.LaMusic.util.AuthUtils;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody CheckoutRequestDTO dto) {
    	UUID userId = AuthUtils.getLoggedUserId();
        OrderResponseDTO order = orderService.placeOrder(userId, dto.getShippingAddressId(), dto.getBillingAddressId());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders() {
        UUID userId = AuthUtils.getLoggedUserId();
        List<Order> orders = orderService.findOrdersByUserId(userId);

        List<OrderResponseDTO> dtoList = orders.stream()
            .map(OrderMapper::mapToDto)
            .toList();

        return ResponseEntity.ok(dtoList);
    }
    
    @GetMapping("/me/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId) {
        UUID userId = AuthUtils.getLoggedUserId();
        Order order = orderService.findOrderByIdAndUser(orderId, userId);
        return ResponseEntity.ok(OrderMapper.mapToDto(order));
    }

}
