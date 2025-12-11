package dev.backend.demo.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

/**
 * 加入購物車請求 DTO
 * 用於接收前端傳來的登入資料
 */
public class AddToCartRequest {
    
    @NotNull(message = "商品ID不能為空")
    @Min(value = 1, message = "商品ID必須大於0")
    private Long productId;
    
    @NotNull(message = "數量不能為空")
    @Min(value = 1, message = "數量必須至少為1")
    @Max(value = 999, message = "數量不能超過999")
    private Integer quantity;
    
    public AddToCartRequest() {}
    
    // Getters and Setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
