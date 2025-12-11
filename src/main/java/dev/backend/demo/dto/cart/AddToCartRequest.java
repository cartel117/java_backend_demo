package dev.backend.demo.dto.cart;

/**
 * 加入購物車請求 DTO
 */
public class AddToCartRequest {
    private Long productId;
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
