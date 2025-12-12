package dev.backend.demo.service;

import dev.backend.demo.exception.ResourceNotFoundException;
import dev.backend.demo.model.Product;
import dev.backend.demo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 產品服務類別
 * 處理產品相關的業務邏輯
 */
@Slf4j
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * 取得所有產品
     * @return 所有產品列表
     */
    public List<Product> getAllProducts() {
        log.debug("取得所有產品");
        List<Product> products = productRepository.findAll();
        log.info("查詢到 {} 個產品", products.size());
        return products;
    }
    
    /**
     * 根據 ID 取得產品
     * @param id 產品 ID
     * @return 產品物件
     * @throws ResourceNotFoundException 如果產品不存在
     */
    public Product getProductById(Long id) {
        log.debug("查詢產品: productId={}", id);
        return productRepository.findById(id)
            .orElseThrow(() -> {
                log.error("產品不存在: productId={}", id);
                return new ResourceNotFoundException("產品不存在：ID = " + id);
            });
    }
    
    /**
     * 新增或更新產品
     * @param product 產品物件
     * @return 儲存後的產品物件
     */
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    /**
     * 刪除產品
     * @param id 產品 ID
     * @throws ResourceNotFoundException 如果產品不存在
     */
    public void deleteProduct(Long id) {
        log.info("刪除產品: productId={}", id);
        
        if (!productRepository.existsById(id)) {
            log.error("產品不存在，無法刪除: productId={}", id);
            throw new ResourceNotFoundException("產品不存在，無法刪除：ID = " + id);
        }
        
        productRepository.deleteById(id);
        log.info("產品刪除成功: productId={}", id);
    }
    
    /**
     * 根據分類 ID 取得產品列表
     * @param categoryId 分類 ID
     * @return 該分類下的所有產品
     */
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}
