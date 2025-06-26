package com.LaMusic.Mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.LaMusic.dto.OrderAddressDTO;
import com.LaMusic.dto.OrderItemResponseDTO;
import com.LaMusic.dto.OrderResponseDTO;
import com.LaMusic.entity.Order;

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

	    OrderAddressDTO shipping = new OrderAddressDTO(
	        order.getShippingAddress().getRecipientName(),
	        order.getShippingAddress().getStreet(),
	        order.getShippingAddress().getNumber(),
	        order.getShippingAddress().getComplement(),
	        order.getShippingAddress().getNeighborhood(),
	        order.getShippingAddress().getCity(),
	        order.getShippingAddress().getState(),
	        order.getShippingAddress().getZipCode(),
	        order.getShippingAddress().getCountry()
	    );

	    OrderAddressDTO billing = new OrderAddressDTO(
	        order.getBillingAddress().getRecipientName(),
	        order.getBillingAddress().getStreet(),
	        order.getBillingAddress().getNumber(),
	        order.getBillingAddress().getComplement(),
	        order.getBillingAddress().getNeighborhood(),
	        order.getBillingAddress().getCity(),
	        order.getBillingAddress().getState(),
	        order.getBillingAddress().getZipCode(),
	        order.getBillingAddress().getCountry()
	    );

	    return new OrderResponseDTO(
	        order.getId(),
	        order.getCreatedAt(),
	        order.getTotalAmount(),
	        shipping,
	        billing,
	        items
	    );
	}

	
}
