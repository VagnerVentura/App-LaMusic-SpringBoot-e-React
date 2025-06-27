package com.LaMusic.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LaMusic.Mappers.CartMapper;
import com.LaMusic.dto.AddCartDTO;
import com.LaMusic.dto.CartResponseDTO;
import com.LaMusic.entity.Cart;
import com.LaMusic.entity.CartItem;
import com.LaMusic.services.CartService;
import com.LaMusic.util.AuthUtils;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<Cart> addItemToCart(@RequestBody @Validated AddCartDTO dto) {
        if (dto.productId() == null || dto.quantity() == null) {
            // Considerar usar Bean Validation para isso com @NotNull nos campos do DTO
            // e @Valid no controller para uma resposta 400 mais automática.
            throw new IllegalArgumentException("ID do produto e quantidade são obrigatórios.");
        }
        if (dto.quantity() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva.");
        }

        UUID userId = AuthUtils.getLoggedUserId();
        Cart cart = cartService.addToCart(userId, dto.productId(), dto.quantity());
        return ResponseEntity.ok(cart);
    }


    @GetMapping
    public ResponseEntity<CartResponseDTO> getMyCart() {
        UUID userId = AuthUtils.getLoggedUserId();
        Cart cart = cartService.findOrCreateCartByUserId(userId);

        // Garante que os itens estejam carregados
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            cart.setItems(cartService.getCartItemsByCartId(cart.getId()));
        }

        return ResponseEntity.ok(CartMapper.toDto(cart));
    }


    @GetMapping("/cart/by-id/{cartId}")
    public ResponseEntity<List<CartItem>> getCartItemsByCartId(@PathVariable UUID cartId) {
        return ResponseEntity.ok(cartService.getCartItemsByCartId(cartId));
    }

    // Remover um item específico do carrinho (por ID do produto)
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDTO> removeItemFromCart(@PathVariable UUID cartItemId) {
        UUID userId = AuthUtils.getLoggedUserId();
        Cart updatedCart = cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.ok(CartMapper.toDto(updatedCart));
    }

    // Esvaziar completamente o carrinho
    @DeleteMapping("/clear")
    public ResponseEntity<CartResponseDTO> clearMyCart() {
        UUID userId = AuthUtils.getLoggedUserId();
        Cart clearedCart = cartService.clearCart(userId);
        return ResponseEntity.ok(CartMapper.toDto(clearedCart));
    }
    
 // Finalizar compra (exemplo)
    @PostMapping("/checkout")
    public ResponseEntity<String> checkoutCart() {
        UUID userId = AuthUtils.getLoggedUserId();
        cartService.checkout(userId); // Você deve ter um método que processa a venda e limpa o carrinho
        return ResponseEntity.ok("Compra finalizada com sucesso!");
    }
}
