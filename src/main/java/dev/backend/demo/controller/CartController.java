package dev.backend.demo.controller;

import dev.backend.demo.dto.cart.AddToCartRequest;
import dev.backend.demo.dto.cart.CartResponseDTO;
import dev.backend.demo.service.CartService;
import dev.backend.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 購物車 API 控制器
 * 使用資料庫持久化購物車資料
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 從 JWT Token 取得使用者 ID
     */
    private Long getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("無效的認證標頭");
        }
        String token = authHeader.substring(7);
        // String username = jwtUtil.extractUsername(token);
        return jwtUtil.extractUserId(token);
    }
    
    /**
     * 取得購物車內容
     * GET /api/cart
     */
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(
            @RequestHeader("Authorization") String authHeader) {
        
        Long userId = getUserIdFromToken(authHeader);
        CartResponseDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 加入商品到購物車
     * POST /api/cart
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddToCartRequest request) {
        
        Long userId = getUserIdFromToken(authHeader);
        CartResponseDTO cart = cartService.addToCart(
            userId, 
            request.getProductId(), 
            request.getQuantity() != null ? request.getQuantity() : 1
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "商品已加入購物車");
        response.put("cart", cart);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新商品數量
     * PUT /api/cart/{productId}
     */
    @PutMapping("/{productId}")
    public ResponseEntity<CartResponseDTO> updateQuantity(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> body) {
        
        Long userId = getUserIdFromToken(authHeader);
        Integer quantity = body.get("quantity");
        CartResponseDTO cart = cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 從購物車移除商品
     * DELETE /api/cart/{productId}
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long productId) {
        
        Long userId = getUserIdFromToken(authHeader);
        CartResponseDTO cart = cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 清空購物車
     * DELETE /api/cart
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearCart(
            @RequestHeader("Authorization") String authHeader) {
        
        Long userId = getUserIdFromToken(authHeader);
        cartService.clearCart(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "購物車已清空");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 結帳（模擬）
     * POST /api/cart/checkout
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(
            @RequestHeader("Authorization") String authHeader) {
        
        Long userId = getUserIdFromToken(authHeader);
        CartResponseDTO cart = cartService.getCart(userId);
        
        if (cart.getItems().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "購物車是空的");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 清空購物車
        cartService.clearCart(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "結帳成功！感謝您的購買！");
        response.put("totalPrice", cart.getTotalPrice());
        response.put("totalItems", cart.getTotalItems());
        
        return ResponseEntity.ok(response);
    }
}
