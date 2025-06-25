package com.LaMusic.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LaMusic.Mappers.OrderMapper;
import com.LaMusic.dto.OrderResponseDTO;
import com.LaMusic.entity.Address;
import com.LaMusic.entity.Cart;
import com.LaMusic.entity.Order;
import com.LaMusic.entity.OrderAddress;
import com.LaMusic.entity.OrderItem;
import com.LaMusic.entity.Product;
import com.LaMusic.repositories.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    AddressService addressService;
    

    public OrderResponseDTO placeOrder(UUID userId, UUID shippingAddressId, UUID billingAddressId) {
        Cart cart = cartService.findCartByUserId(userId);

        Address shipping = addressService.findById(shippingAddressId);
        Address billing = addressService.findById(billingAddressId);

        OrderAddress shippingOrderAddress = buildOrderAddressFromAddress(shipping);
        OrderAddress billingOrderAddress = buildOrderAddressFromAddress(billing);

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress(shippingOrderAddress);
        order.setBillingAddress(billingOrderAddress);

        List<OrderItem> items = cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productService.updateProductStock(product);

            return OrderItem.builder()
                .product(product)
                .order(order)
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getPrice())
                .build();
        }).collect(Collectors.toList());

        order.setItems(items);
        order.setTotalAmount(
            items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        orderRepository.save(order);
        cartService.deleteCart(cart);

        return OrderMapper.mapToDto(order);
    }
    public List<Order> findOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId);
    }
    
    private OrderAddress buildOrderAddressFromAddress(Address address) {
	    return OrderAddress.builder()
	        .recipientName(address.getRecipientName())
	        .street(address.getStreet())
	        .number(address.getNumber())
	        .complement(address.getComplement())
	        .neighborhood(address.getNeighborhood())
	        .city(address.getCity())
	        .state(address.getState())
	        .zipCode(address.getZipCode())
	        .country(address.getCountry())
	        .createdAt(OffsetDateTime.now())
	        .build();
	}
}
