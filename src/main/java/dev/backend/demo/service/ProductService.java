package dev.backend.demo.service;

import dev.backend.demo.model.Product;
import dev.backend.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 產品服務類別
 * 處理產品相關的業務邏輯
 */
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * 取得所有產品
     * @return 所有產品列表
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * 根據 ID 取得產品
     * @param id 產品 ID
     * @return 產品物件，如果不存在則回傳 null
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
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
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
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
