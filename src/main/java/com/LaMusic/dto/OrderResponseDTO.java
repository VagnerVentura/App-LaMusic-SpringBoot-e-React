package com.LaMusic.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
    UUID orderId,
    LocalDate orderDate,
    BigDecimal totalAmount,
    String status,
    OrderAddressDTO shippingAddress,
    OrderAddressDTO billingAddress,
    List<OrderItemResponseDTO> items
) {}
