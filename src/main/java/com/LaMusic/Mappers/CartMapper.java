package com.LaMusic.Mappers;


import java.util.List;
import java.util.stream.Collectors;

import com.LaMusic.dto.CartItemResponseDTO;
import com.LaMusic.dto.CartResponseDTO;
import com.LaMusic.dto.ProductImageResponseDTO;
import com.LaMusic.dto.ProductResponseCartDTO;
import com.LaMusic.entity.Cart;

public class CartMapper {

    public static CartResponseDTO toDto(Cart cart) {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(cart.getId());
        dto.setStatus(cart.getStatus());

        List<CartItemResponseDTO> items = cart.getItems().stream().map(item -> {
            CartItemResponseDTO itemDto = new CartItemResponseDTO();
            itemDto.setId(item.getId());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());

            ProductResponseCartDTO productDto = new ProductResponseCartDTO();
            productDto.setId(item.getProduct().getId());
            productDto.setName(item.getProduct().getName());
            productDto.setPrice(item.getProduct().getPrice());

            List<ProductImageResponseDTO> imageDtos = item.getProduct().getImages().stream()
                .map(img -> {
                    ProductImageResponseDTO imgDto = new ProductImageResponseDTO();
                    imgDto.setId(img.getId());
                    imgDto.setImageUrl(img.getUrl()); // Supondo que imageUrl é o campo no banco
                    return imgDto;
                })
                .collect(Collectors.toList());

            productDto.setImages(imageDtos);
            itemDto.setProduct(productDto);
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }
}
