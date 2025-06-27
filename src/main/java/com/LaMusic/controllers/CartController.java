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

import com.LaMusic.dto.AddCartDTO;
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
    public ResponseEntity<Cart> getMyCart() {
        UUID userId = AuthUtils.getLoggedUserId();
        Cart cart = cartService.findOrCreateCartByUserId(userId);
        // Garante que os itens sejam carregados e retornados com o carrinho.
        // O CartService.addToCart já faz isso. Para consistência, findOrCreateCartByUserId
        // no service poderia também popular os itens, ou fazemos aqui.
        // A implementação atual de addToCart no service já popula os itens.
        // Se findOrCreateCartByUserId não popula, precisamos fazer aqui:
        if (cart.getItems() == null || cart.getItems().isEmpty()) { // Otimização: só carrega se não tiver
             // ou se a estratégia de fetch for LAZY e não foram carregados.
             // A implementação atual de addToCart no service já popula os itens.
             // findOrCreateCartByUserId não popula.
             // Para garantir, vamos popular aqui.
            cart.setItems(cartService.getCartItemsByCartId(cart.getId()));
        }
        return ResponseEntity.ok(cart);
    }

/*     @DeleteMapping("/items") 
    public ResponseEntity<Void> clearMyCart() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    } */

    @GetMapping("/cart/by-id/{cartId}")
    public ResponseEntity<List<CartItem>> getCartItemsByCartId(@PathVariable UUID cartId) {
        return ResponseEntity.ok(cartService.getCartItemsByCartId(cartId));
    }

    @DeleteMapping("/items/{productId}") // Ou /items/{cartItemId} se você usar o ID do CartItem
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable UUID productId) {
    	UUID userId = AuthUtils.getLoggedUserId();
        Cart updatedCart = cartService.removeItemFromCart(userId, productId); // Novo método no CartService
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/clear") // Ou o endpoint que o frontend está usando para limpar o carrinho
    public ResponseEntity<Cart> clearMyCart() { // Alterado de ResponseEntity<Void> para ResponseEntity<Cart>
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart clearedCart = cartService.clearCart(userId); // cartService.clearCart agora retorna o Cart
        return ResponseEntity.ok(clearedCart); // Retorna o carrinho (vazio)
    }
}
