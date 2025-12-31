package dev.backend.demo.service;

import dev.backend.demo.exception.ResourceNotFoundException;
import dev.backend.demo.model.Product;
import dev.backend.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProductService 單元測試
 * 使用 Mockito 模擬 Repository 層，確保測試只專注於 Service 層的業務邏輯。
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    // 模擬資料庫存取層，不會真的去跑資料庫
    @Mock
    private ProductRepository productRepository;
    
    // 將模擬好的 Mock 對象注入到 ProductService 中
    @InjectMocks
    private ProductService productService;
    
    /**
     * 測試：獲取所有產品清單
     * 邏輯：Repository 應回傳一組 List，Service 應原封不動地回傳該 List。
     */
    @Test
    void testGetAllProducts() {
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");
        
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");
        
        // [Given] 定義 Mock 行為：當呼叫 findAll 時，回傳準備好的假清單
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        
        List<Product> result = productService.getAllProducts();
        
        // [Then] 驗證結果：回傳數量應為 2
        assertEquals(2, result.size());
        // [Then] 確認 repository 的 findAll() 確實被呼叫了 1 次
        verify(productRepository, times(1)).findAll();
    }
    
    /**
     * 測試：根據產品 ID 獲取產品 (成功路徑)
     * 邏輯：當 ID 存在時，應回傳對應的產品對象。
     */
    @Test
    void testGetProductById() {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");
        
        // [Given] 定義 Mock 行為：給予 ID 1L 時回傳 Optional 包裝的產品
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        // [When] 執行測試
        Product result = productService.getProductById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
    }
    
    /**
     * 測試：產品不存在時拋出異常
     */
    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999L);
        });
    }
    
    /**
     * 測試：創建產品
     */
    @Test
    void testCreateProduct() {
        Product newProduct = new Product();
        newProduct.setProductName("New Product");
        newProduct.setUnitPrice(new BigDecimal("99.99"));
        
        Product savedProduct = new Product();
        savedProduct.setProductId(1L);
        savedProduct.setProductName("New Product");
        
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        
        Product result = productService.saveProduct(newProduct);
        
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    /**
     * 測試：更新產品
     */
    @Test
    void testUpdateProduct() {
        Product existingProduct = new Product();
        existingProduct.setProductId(1L);
        existingProduct.setProductName("Old Name");
        
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
        
        // 更新現有產品的屬性
        existingProduct.setProductName("Updated Name");
        existingProduct.setUnitPrice(new BigDecimal("150.00"));
        Product result = productService.saveProduct(existingProduct);
        
        assertEquals("Updated Name", result.getProductName());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    /**
     * 測試：刪除產品
     */
    @Test
    void testDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);
        
        productService.deleteProduct(1L);
        
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }
}
