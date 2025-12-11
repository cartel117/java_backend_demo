package dev.backend.demo.service;

import dev.backend.demo.dto.cart.CartItemDTO;
import dev.backend.demo.dto.cart.CartResponseDTO;
import dev.backend.demo.exception.InvalidOperationException;
import dev.backend.demo.exception.ResourceNotFoundException;
import dev.backend.demo.model.Cart;
import dev.backend.demo.model.CartItem;
import dev.backend.demo.model.Product;
import dev.backend.demo.repository.CartRepository;
import dev.backend.demo.repository.CartItemRepository;
import dev.backend.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 購物車服務
 * 使用資料庫持久化購物車資料
 */
@Service
@Transactional
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * 取得或建立使用者的購物車
     */
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                return cartRepository.save(newCart);
            });
    }
    
    /**
     * 加入商品到購物車
     */
    public CartResponseDTO addToCart(Long userId, Long productId, Integer quantity) {
        // 驗證數量
        if (quantity == null || quantity <= 0) {
            throw new InvalidOperationException("商品數量必須大於 0");
        }
        if (quantity > 999) {
            throw new InvalidOperationException("商品數量不能超過 999");
        }
        
        // 驗證商品是否存在
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("商品不存在：ID = " + productId));
        
        // 取得或建立購物車
        Cart cart = getOrCreateCart(userId);
        
        // 檢查商品是否已在購物車中
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId)
            .orElse(null);
        
        if (cartItem != null) {
            // 更新數量
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // 新增項目
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getUnitPrice());
        }
        
        cartItemRepository.save(cartItem);
        
        return getCart(userId);
    }
    
    /**
     * 取得購物車內容
     */
    @Transactional(readOnly = true)
    public CartResponseDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                return cartRepository.save(newCart);
            });
        
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
            .map(item -> {
                // 手動載入 Product 以避免 LazyInitializationException
                Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("商品不存在：ID = " + item.getProductId()));
                
                return new CartItemDTO(
                    item.getCartItemId(),
                    item.getProductId(),
                    product.getProductName(),
                    item.getUnitPrice(),
                    item.getQuantity(),
                    item.getSubtotal()
                );
            })
            .collect(Collectors.toList());
        
        Integer totalItems = itemDTOs.stream()
            .mapToInt(CartItemDTO::getQuantity)
            .sum();
        
        BigDecimal totalPrice = itemDTOs.stream()
            .map(CartItemDTO::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new CartResponseDTO(cart.getCartId(), itemDTOs, totalItems, totalPrice);
    }
    
    /**
     * 更新商品數量
     */
    public CartResponseDTO updateQuantity(Long userId, Long productId, Integer quantity) {
        // 驗證數量
        if (quantity == null) {
            throw new InvalidOperationException("商品數量不能為空");
        }
        if (quantity < 0) {
            throw new InvalidOperationException("商品數量不能小於 0");
        }
        if (quantity > 999) {
            throw new InvalidOperationException("商品數量不能超過 999");
        }
        
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("購物車不存在：User ID = " + userId));
        
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId)
            .orElseThrow(() -> new ResourceNotFoundException("商品不在購物車中：Product ID = " + productId));
        
        if (quantity == 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
        
        return getCart(userId);
    }
    
    /**
     * 從購物車移除商品
     */
    public CartResponseDTO removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("購物車不存在：User ID = " + userId));
        
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId)
            .orElseThrow(() -> new ResourceNotFoundException("商品不在購物車中：Product ID = " + productId));
        
        cartItemRepository.delete(cartItem);
        
        return getCart(userId);
    }
    
    /**
     * 清空購物車
     */
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElse(null);
        
        if (cart != null) {
            cartItemRepository.deleteByCartId(cart.getCartId());
        }
    }
}
