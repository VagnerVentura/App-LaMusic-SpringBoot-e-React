package com.LaMusic.services;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.LaMusic.entity.Cart;
import com.LaMusic.entity.CartItem;
import com.LaMusic.entity.Product;
import com.LaMusic.entity.User;
import com.LaMusic.repositories.CartItemRepository;
import com.LaMusic.repositories.CartRepository;
import com.LaMusic.repositories.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductRepository productRepository, UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    @Transactional
    public Cart addToCart(UUID userId, UUID productId, Integer quantity) {
        logger.info("addToCart - Início: userId={}, productId={}, quantity={}", userId, productId, quantity);

        Cart cart = findOrCreateCartByUserId(userId);
        logger.debug("addToCart - Carrinho obtido/criado: cartId={}, userId={}, itemsCount={}", 
            cart.getId(), (cart.getUser() != null ? cart.getUser().getId() : "null"), cart.getItems() != null ? cart.getItems().size() : "null");

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> {
                logger.error("addToCart - Produto não encontrado: productId={}", productId);
                return new RuntimeException("Produto não encontrado: " + productId);
            });
        logger.debug("addToCart - Produto encontrado: productId={}, productName='{}'", product.getId(), product.getName());

        CartItem item = cart.getItems().stream()
            .filter(ci -> ci.getProduct() != null && ci.getProduct().getId().equals(productId))
            .findFirst()
            .orElseGet(() -> {
                logger.info("addToCart - Criando novo CartItem para productId={} no cartId={}", productId, cart.getId());
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setPrice(product.getPrice()); // Definir o preço no momento da adição
                cart.getItems().add(newItem); 
                return newItem;
            });
        
        // Atualizar o preço do item caso o preço do produto tenha mudado desde a última vez que foi adicionado.
        // Isso é opcional e depende da regra de negócio. Se o preço deve ser fixado no momento da adição,
        // o setPrice acima no orElseGet é suficiente para novos itens. Para itens existentes, pode-se optar por não atualizar
        // ou atualizar aqui.
        // item.setPrice(product.getPrice()); 

        logger.debug("addToCart - CartItem (itemId={}) antes da atualização da quantidade: currentQuantity={}",
            item.getId(), item.getQuantity());
        
        item.setQuantity(quantity);

        logger.info("addToCart - CartItem (itemId={}) atualizado: newQuantity={}", 
            item.getId(), item.getQuantity());

        if (item.getQuantity() <= 0) {
            logger.info("addToCart - Quantidade <= 0, removendo CartItem: itemId={}, productId={}", item.getId(), productId);
            cart.getItems().remove(item);
            if (item.getId() != null) { 
                 cartItemRepository.delete(item);
            }
            logger.debug("addToCart - CartItem removido da coleção: itemId={}", item.getId());
        } else {
            CartItem savedItem = cartItemRepository.save(item); // Salva ou atualiza o item
            logger.debug("addToCart - CartItem salvo/atualizado: itemId={}, productId={}, quantity={}", 
                savedItem.getId(), savedItem.getProduct().getId(), savedItem.getQuantity());
            // Se o item era novo e foi adicionado à coleção 'cart.getItems()',
            // e não estava na lista antes do save(item), pode ser necessário re-adicioná-lo
            // ou garantir que a instância 'item' seja a gerenciada.
            // No entanto, como 'item' é obtido do stream ou criado e adicionado à coleção do cart,
            // e depois salvo, o Hibernate deve gerenciar corretamente.
        }
        
        Cart savedCart = cartRepository.save(cart); // Salva o carrinho, o que deve cascatear as alterações nos itens
        logger.debug("addToCart - Carrinho salvo: cartId={}", savedCart.getId());
        
        if (logger.isDebugEnabled()) {
            String itemsDetails = savedCart.getItems().stream()
                .map(ci -> String.format("Item[id=%s, prodId=%s, qty=%d]", ci.getId(), (ci.getProduct() != null ? ci.getProduct().getId() : "null"), ci.getQuantity()))
                .collect(Collectors.joining(", "));
            logger.debug("addToCart - Itens finais no carrinho: cartId={}, items=[{}]", savedCart.getId(), itemsDetails);
        }
        logger.info("addToCart - Fim: cartId={}, userId={}", savedCart.getId(), userId);
        return savedCart;
    }

    @Transactional
    public Cart removeItemFromCart(UUID userId, UUID cartItemId) {
        logger.info("removeItemFromCart - Início: userId={}, cartItemId={}", userId, cartItemId);

        Cart cart = findCartByUserId(userId);
        logger.debug("removeItemFromCart - Carrinho encontrado: cartId={}", cart.getId());

        CartItem itemToRemove = cart.getItems().stream()
            .filter(item -> item.getId().equals(cartItemId))
            .findFirst()
            .orElse(null);

        if (itemToRemove != null) {
            cart.getItems().remove(itemToRemove); // modifica a coleção existente
            cartItemRepository.delete(itemToRemove); // opcional se orphanRemoval=true
            logger.info("CartItem removido com sucesso: {}", itemToRemove.getId());
        } else {
            logger.warn("CartItem não encontrado no carrinho: cartItemId={}", cartItemId);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart clearCart(UUID userId) { // Modificado para retornar Cart
        logger.info("clearCart - Início: userId={}", userId);
        Cart cart = findCartByUserId(userId);
        logger.debug("clearCart - Carrinho encontrado: cartId={}", cart.getId());
        
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            logger.info("clearCart - Removendo {} itens do cartId={}", cart.getItems().size(), cart.getId());
            // Para orphanRemoval=true, limpar a coleção e salvar o pai é suficiente.
            // A deleção explícita garante, mas pode ser redundante.
            cartItemRepository.deleteAllInBatch(new ArrayList<>(cart.getItems())); // Mais eficiente para múltiplos deletes
            cart.getItems().clear(); 
            logger.debug("clearCart - Itens deletados e coleção em memória limpa.");
        } else {
            logger.info("clearCart - Carrinho já estava vazio ou sem itens: cartId={}", cart.getId());
        }
        Cart savedCart = cartRepository.save(cart); // Salva o carrinho com a lista de itens vazia
        logger.info("clearCart - Fim: userId={}, cartId={}", userId, savedCart.getId());
        return savedCart; // Retorna o carrinho (agora vazio)
    }

    // Este método é auxiliar e não precisa de alteração para o contrato de resposta.
    public List<CartItem> getCartItemsByCartId(UUID cartId) {
        logger.debug("getCartItemsByCartId - Buscando itens para cartId={}", cartId);
        List<CartItem> items = cartItemRepository.findByCart_Id(cartId);
        logger.debug("getCartItemsByCartId - Encontrados {} itens para cartId={}", (items != null ? items.size() : "null"), cartId);
        return items;
    }

    public Cart findOrCreateCartByUserId(UUID userId) {
        logger.debug("findOrCreateCartByUserId - Início: userId={}", userId);
        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> { 
                logger.info("findOrCreateCartByUserId - Carrinho não encontrado para userId={}, criando novo.", userId);
                User user = userService.findById(userId);
                if (user == null) {
                    logger.error("findOrCreateCartByUserId - Usuário não encontrado com ID: {}", userId);
                    throw new RuntimeException("Usuário não encontrado para criar carrinho: " + userId);
                }
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setStatus("active");
                // A entidade Cart já deve inicializar 'items = new ArrayList<>()'
                // newCart.setItems(new ArrayList<>()); // Desnecessário se inicializado na entidade
                Cart savedNewCart = cartRepository.save(newCart);
                logger.info("findOrCreateCartByUserId - Novo carrinho criado e salvo: cartId={}, userId={}", 
                    savedNewCart.getId(), userId);
                return savedNewCart;
            });

        // A coleção 'items' é gerenciada pelo Hibernate.
        // Se for LAZY, será carregada ao ser acessada (ex: pela serialização JSON).
        // A inicialização 'private List<CartItem> items = new ArrayList<>();' na entidade Cart
        // garante que cart.getItems() nunca seja null.
        // A verificação abaixo é uma salvaguarda, mas idealmente não seria necessária.
        if (cart.getItems() == null) {
            logger.warn("findOrCreateCartByUserId - Coleção de itens era nula para cartId={}, inicializando com nova lista. Verifique a inicialização na entidade Cart.", cart.getId());
            cart.setItems(new ArrayList<>());
        }
        
        logger.debug("findOrCreateCartByUserId - Fim: cartId={}, userId={}, itemsCount={}", 
            cart.getId(), (cart.getUser() != null ? cart.getUser().getId() : "null"), (cart.getItems() != null ? cart.getItems().size() : "null"));
        return cart;
    }

    public Cart findCartByUserId(UUID userId) {
        logger.debug("findCartByUserId - Início: userId={}", userId);
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> {
                logger.error("findCartByUserId - Carrinho não encontrado para userId={}", userId);
                return new RuntimeException("Carrinho não encontrado para o usuário: " + userId);
            });
        
        if (cart.getItems() == null) {
            logger.warn("findCartByUserId - Coleção de itens era nula para cartId={}, inicializando. Verifique a inicialização na entidade Cart.", cart.getId());
            cart.setItems(new ArrayList<>());
        }

        logger.debug("findCartByUserId - Fim: cartId={}, userId={}, itemsCount={}", 
            cart.getId(), (cart.getUser() != null ? cart.getUser().getId() : "null"), (cart.getItems() != null ? cart.getItems().size() : "null"));
        return cart;
    }

    @Transactional
    public void deleteCart(Cart cart) { // Este método não é diretamente exposto pela API do carrinho do usuário, mas pode ser usado internamente
        logger.info("deleteCart - Início: cartId={}", cart.getId());
        // Se CascadeType.ALL ou REMOVE estiver na relação Cart -> CartItems,
        // a deleção dos itens será automática.
        // Caso contrário, a exclusão manual é necessária.
        // A inicialização da lista na entidade Cart garante que getItems() não seja nulo.
        if (!cart.getItems().isEmpty()) {
            logger.debug("deleteCart - Deletando {} itens do cartId={}", cart.getItems().size(), cart.getId());
            cartItemRepository.deleteAllInBatch(new ArrayList<>(cart.getItems())); // Passa uma cópia para evitar ConcurrentModificationException se a coleção for modificada durante a iteração
            cart.getItems().clear(); // Limpa a coleção em memória
        } else {
            logger.debug("deleteCart - Nenhum item encontrado para deletar no cartId={}", cart.getId());
        }
        
        cartRepository.delete(cart);
        logger.info("deleteCart - Fim: Carrinho deletado com sucesso: cartId={}", cart.getId());
    }
    
    @Transactional
    public void checkout(UUID userId) {
        logger.info("checkout - Início: userId={}", userId);

        Cart cart = findCartByUserId(userId);

        if (cart.getItems().isEmpty()) {
            logger.warn("checkout - Tentativa de finalizar carrinho vazio: cartId={}", cart.getId());
            throw new IllegalStateException("Não é possível finalizar um carrinho vazio.");
        }

        // Aqui você pode implementar a lógica de criação de pedido (Order), salvar no banco etc.
        // Exemplo básico (sem persistência de pedido):
        logger.info("checkout - Finalizando compra para cartId={} com {} itens.", cart.getId(), cart.getItems().size());

        // Limpa o carrinho após o checkout
        cartItemRepository.deleteAllInBatch(new ArrayList<>(cart.getItems()));
        cart.getItems().clear();
        cartRepository.save(cart);

        logger.info("checkout - Compra finalizada com sucesso: cartId={}, userId={}", cart.getId(), userId);
    }
    
}