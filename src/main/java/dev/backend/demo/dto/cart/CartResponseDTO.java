package dev.backend.demo.dto.cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * 購物車回應 DTO
 * 用於接收前端傳來的登入資料
 */
public class CartResponseDTO {
    private Long cartId;
    private List<CartItemDTO> items;
    private Integer totalItems;
    private BigDecimal totalPrice;
    
    public CartResponseDTO() {}
    
    public CartResponseDTO(Long cartId, List<CartItemDTO> items, 
                          Integer totalItems, BigDecimal totalPrice) {
        this.cartId = cartId;
        this.items = items;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
    }
    
    // Getters and Setters
    public Long getCartId() {
        return cartId;
    }
    
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
    
    public List<CartItemDTO> getItems() {
        return items;
    }
    
    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
    
    public Integer getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
