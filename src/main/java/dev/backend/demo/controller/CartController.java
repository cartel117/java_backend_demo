package dev.backend.demo.controller;

import dev.backend.demo.dto.cart.AddToCartRequest;
import dev.backend.demo.dto.cart.CartResponseDTO;
import dev.backend.demo.exception.UnauthorizedException;
import dev.backend.demo.service.CartService;
import dev.backend.demo.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 購物車 API 控制器
 * 使用資料庫持久化購物車資料
 */
@Slf4j
@RestController
@RequestMapping("/api/cart")
@Tag(name = "購物車 API", description = "購物車管理相關 API（需要 JWT 認證）")
@SecurityRequirement(name = "Bearer Authentication")
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
            throw new UnauthorizedException("無效的認證標頭，請先登入");
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
    // 處理 HTTP POST 請求的對映。如果類別級別沒有指定路徑，則對映到根路徑
    @PostMapping
    public ResponseEntity<Map<String, Object>> addToCart(
            // 從 HTTP Header 中獲取名為 "Authorization" 的值，通常包含 JWT 等認證 Token
            @RequestHeader("Authorization") String authHeader,
            // 1. @RequestBody: 將 HTTP 請求的 JSON 主體轉換為 AddToCartRequest 物件。
            // 2. @Valid: 觸發 Bean Validation 機制，檢查 AddToCartRequest 內的欄位是否符合註解定義的約束（例如：productId 必須非空）。
            @Valid @RequestBody AddToCartRequest request) {

        // 步驟 1: 認證與授權 - 從 Token 中提取使用者 ID
        // 呼叫輔助方法解析 Authorization Token，以確定是哪個使用者在操作
        Long userId = getUserIdFromToken(authHeader);
        // 步驟 2: 業務邏輯處理 - 將商品加入購物車
        // 呼叫 Service 層方法執行核心業務邏輯
        CartResponseDTO cart = cartService.addToCart(
            userId, // 當前操作的使用者 ID
            request.getProductId(), // 請求中指定要加入的商品 ID
            // 檢查請求中的數量 (quantity) 是否為 null。
            // 如果是 null，預設數量為 1；否則使用請求提供的數量。
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
