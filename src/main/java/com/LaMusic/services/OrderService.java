package com.LaMusic.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LaMusic.entity.Cart;
import com.LaMusic.entity.Order;
import com.LaMusic.entity.OrderItem;
import com.LaMusic.entity.Product;
import com.LaMusic.entity.User;
import com.LaMusic.repositories.CartRepository;
import com.LaMusic.repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CartService cartService;
	
	@Autowired
	private ProductService productService;
	
	public Order placeOrder(Long userId) {
		Cart cart = cartService.FindCartByUserId(userId);
		
		Order order = new Order();
		order.setUser(cart.getUser());
		order.setOrderDate(LocalDateTime.now());
		
		List<OrderItem> orderItems = cart
				.getItens()
				.stream()
				.map(cartItem -> {
					Product product = cartItem.getProduct();
								product.setStock(product.getStock() - cartItem.getQuantity());
								productService.updateProductStock(product);								
						
						return new OrderItem(null,
						cartItem.getProduct(),
						order,
						cartItem.getQuantity(),
						cartItem.getPrice());
				})
				.collect(
						Collectors.toList());
		
		order.setItems(orderItems);
        order.setTotalAmount(orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        		
        orderRepository.save(order);
        cartService.deleteCart(cart);

        return order;			
	}

	public List<Order> findOrdersByUserId(Long userId) {
		var orders = orderRepository.findByUserId(userId);
		return orders;
	}
	
	
}
