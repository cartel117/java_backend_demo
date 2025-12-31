package dev.backend.demo.exception;

/**
 * 產品未找到異常
 * 當查詢的產品不存在時拋出此異常
 */
public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public ProductNotFoundException(Long productId) {
        super("找不到產品，ID: " + productId);
    }
}
