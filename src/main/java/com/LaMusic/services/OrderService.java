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
import com.LaMusic.entity.Payment;
import com.LaMusic.entity.Product;
import com.LaMusic.repositories.OrderAddressRepository;
import com.LaMusic.repositories.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderAddressRepository orderAddressRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;
    
    @Transactional
    public OrderResponseDTO placeOrder(UUID userId, UUID shippingAddressId, UUID billingAddressId) {
        Cart cart = cartService.findCartByUserId(userId);

        Address shipping = addressService.findById(shippingAddressId);
        Address billing = addressService.findById(billingAddressId);

        // Construir e salvar endereços do pedido
        OrderAddress shippingOrderAddress = buildOrderAddressFromAddress(shipping);
        orderAddressRepository.save(shippingOrderAddress);

        OrderAddress billingOrderAddress = buildOrderAddressFromAddress(billing);
        orderAddressRepository.save(billingOrderAddress);

        // Criar o pedido
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress(shippingOrderAddress);
        order.setBillingAddress(billingOrderAddress);

        // Gerar número do pedido (corrige o erro do banco)
        String orderNumber = generateOrderNumber();
        order.setOrderNumber(orderNumber);

        // Criar itens do pedido e atualizar estoque
        List<OrderItem> items = cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();

            // Atualiza estoque
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productService.updateProductStock(product); // salva imediatamente

            // Evita conflito por reaproveitar a entidade gerenciada diretamente
            Product detachedProduct = Product.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .build();

            BigDecimal unitPrice = cartItem.getPrice();
            Integer quantity = cartItem.getQuantity();
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

            return OrderItem.builder()
                .product(detachedProduct) // agora é seguro
                .order(order)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .productNameSnapshot(product.getName())
                .productSkuSnapshot(product.getSku())
                .createdAt(OffsetDateTime.now())
                .build();
        }).collect(Collectors.toList());
        order.setItems(items);

        BigDecimal subtotal = items.stream()
        	    .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        	    .reduce(BigDecimal.ZERO, BigDecimal::add);

        	order.setSubtotal(subtotal);

        	// Se você tiver frete, cupom, etc., ajuste aqui:
        	order.setTotalAmount(subtotal); // ou subtotal + frete - desconto


        // Salvar pedido e deletar carrinho
        orderRepository.saveAndFlush(order);
        cart.getItems().clear();
        
     // Criar pagamento inicial vinculado ao pedido
        Payment payment = Payment.builder()
        	    .order(order)
        	    .amount(order.getTotalAmount())
        	    .method("pix") // ou "unpaid" – o método pode ser definido depois pelo usuário
        	    .status("pending")
        	    .createdAt(OffsetDateTime.now())
        	    .build();
        
        paymentService.create(payment);        
        
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

    private String generateOrderNumber() {
        String date = LocalDate.now().toString().replace("-", "");
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + date + "-" + random;
    }

	public Order findOrderByIdAndUser(UUID orderId, UUID userId) {
		return  orderRepository.findByIdAndUserId(orderId, userId)
		  .orElseThrow(() -> new RuntimeException("Pedido não encontrado ou não pertence ao usuário."));
	}
}
