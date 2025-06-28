package com.LaMusic.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.LaMusic.dto.OrderAddressDTO;
import com.LaMusic.dto.OrderItemResponseDTO;
import com.LaMusic.dto.OrderResponseDTO;
import com.LaMusic.entity.Order;
import com.LaMusic.entity.OrderAddress;
import com.LaMusic.entity.Product;
import com.LaMusic.entity.ProductImage;

@Component
public class OrderMapper {

    public static OrderResponseDTO mapToDto(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
            .map(item -> {
                Product product = item.getProduct();
                List<ProductImage> imagens = Optional.ofNullable(product.getImages())
                        .orElse(Collections.emptyList());

                String imageUrl = imagens.stream()
                        // .filter(ProductImage::getIsPrimary) // se quiser priorizar uma imagem principal
                        .findFirst()
                        .map(ProductImage::getUrl)
                        .orElse(null);

                return new OrderItemResponseDTO(
                    product.getId(),
                    product.getName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    imageUrl
                );
            }).toList();

        return new OrderResponseDTO(
            order.getId(),
            order.getCreatedAt(),
            order.getTotalAmount(),
            order.getStatus(),
            mapToAddressDto(order.getShippingAddress()),
            mapToAddressDto(order.getBillingAddress()),
            items
        );
    }

    private static OrderAddressDTO mapToAddressDto(OrderAddress address) {
        if (address == null) return null;

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