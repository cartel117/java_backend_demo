package dev.backend.demo.repository;

import dev.backend.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 商品資料存取層 (Repository/DAO)
 * 
 * 主要職責：
 * 1. 提供資料庫 CRUD 操作方法（新增、查詢、修改、刪除）
 * 2. 定義自訂查詢方法（依條件查詢商品）
 * 3. 與資料庫溝通：透過 JPA/Hibernate 執行 SQL 語句
 * 
 * 比喻：像是「倉庫管理員」，負責從資料庫存取商品
 * 
 * 繼承 JpaRepository 後自動獲得以下方法：
 * - save(Product) : 新增或更新商品
 * - findById(Long) : 根據ID查詢商品
 * - findAll() : 查詢所有商品
 * - deleteById(Long) : 根據ID刪除商品
 * - count() : 計算商品總數
 * 
 * 注意：實際的物件與資料庫轉換由 JPA/Hibernate 框架負責
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * 根據產品名稱查詢產品
     * 
     * Spring Data JPA 命名規則：
     * findBy + 屬性名稱 → 自動生成查詢語句
     * 等同於 SQL: SELECT * FROM products WHERE product_name = ?
     */
    Product findByProductName(String productName);
    
    /**
     * 根據分類 ID 查詢產品列表
     * 
     * 等同於 SQL: SELECT * FROM products WHERE category_id = ?
     * 返回符合條件的所有商品
     */
    java.util.List<Product> findByCategoryId(Long categoryId);
}
