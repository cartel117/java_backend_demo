package dev.backend.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品實體類別 (Entity)
 * 
 * 主要職責：
 * 1. 定義物件結構：描述商品有哪些屬性（欄位）
 * 2. ORM 映射：透過 JPA 註解將資料庫 table 對應成 Java 物件
 * 3. 封裝資料：提供 getter/setter 方法存取和修改屬性
 * 
 * 比喻：像是「商品的設計圖」，定義商品長什麼樣子
 * 
 * 注意：實際的資料庫操作由 ProductRepository 負責
 */
@Entity  // 標記為 JPA 實體類別
@Table(name = "products")  // 對應資料庫的 products 表
public class Product {

    // 商品ID - 主鍵，由資料庫自動生成
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    // 商品名稱
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    // 類別ID - 外鍵，關聯到 categories 表
    // 註：移除外鍵約束，允許自由設定類別 ID
    @Column(name = "category_id", nullable = true)
    private Long categoryId;

    // 供應商ID - 外鍵，關聯到 suppliers 表  
    // 註：移除外鍵約束，允許自由設定供應商 ID
    @Column(name = "supplier_id", nullable = true)
    private Long supplierId;

    // 商品描述
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 單價
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    // 建立時間 - 只在新增時設定，之後不可修改
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新時間 - 每次修改時自動更新
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== 建構子 ==========
    
    /**
     * 預設建構子（JPA 需要）
     */
    public Product() {
    }

    /**
     * 參數化建構子 - 方便建立新商品物件
     */
    public Product(String productName, Long categoryId, Long supplierId, String description, BigDecimal unitPrice) {
        this.productName = productName;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.description = description;
        this.unitPrice = unitPrice;
    }

    // ========== 生命週期回調方法 ==========
    
    /**
     * 在資料儲存到資料庫之前自動執行
     * 用於設定建立時間和更新時間
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * 在資料更新到資料庫之前自動執行
     * 用於更新修改時間
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ========== Getters and Setters ==========
    // 提供存取和修改屬性的方法
    
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", categoryId=" + categoryId +
                ", supplierId=" + supplierId +
                ", description='" + description + '\'' +
                ", unitPrice=" + unitPrice +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
