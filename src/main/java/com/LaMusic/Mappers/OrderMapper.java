package com.LaMusic.Mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.LaMusic.dto.OrderAddressDTO;
import com.LaMusic.dto.OrderItemResponseDTO;
import com.LaMusic.dto.OrderResponseDTO;
import com.LaMusic.entity.Order;
import com.LaMusic.entity.OrderAddress;

@Component
public class OrderMapper {

    public static OrderResponseDTO mapToDto(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
            .map(item -> new OrderItemResponseDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice()
            )).toList();

        OrderAddressDTO shipping = mapToAddressDto(order.getShippingAddress());
        OrderAddressDTO billing = mapToAddressDto(order.getBillingAddress());

        return new OrderResponseDTO(
            order.getId(),
            order.getOrderDate(),
            order.getTotalAmount(),
            shipping,
            billing,
            items
        );
    }

    private static OrderAddressDTO mapToAddressDto(OrderAddress address) {
        if (address == null) {
            return null;
        }

        return new OrderAddressDTO(
            address.getRecipientName(),
            address.getStreet(),
            address.getNumber(),
            address.getComplement(),
            address.getNeighborhood(),
            address.getCity(),
            address.getState(),
            address.getZipCode(),
            address.getCountry()
        );
    }
}