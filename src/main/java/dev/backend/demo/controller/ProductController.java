package dev.backend.demo.controller;

import dev.backend.demo.model.Product;
import dev.backend.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * 產品控制器 (RESTful API)
 * 
 * RESTful API 設計原則：
 * - 使用 HTTP 方法表達操作意圖：GET(查詢)、POST(新增)、PUT(更新)、DELETE(刪除)
 * - URL 代表資源，使用名詞而非動詞
 * - 使用標準 HTTP 狀態碼
 * - 資源的 ID 放在 URL 路徑中
 */
@RestController
@RequestMapping("/api/products")  // RESTful: 資源為複數名詞
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 取得所有產品
     * GET /api/products
     * 
     * RESTful 設計：
     * - 使用 GET 方法查詢資源集合
     * - 直接回傳資源陣列，不包裝在 response 物件中
     * - HTTP 200 OK 表示成功
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(HttpSession session) {
        // 驗證使用者登入狀態
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    /**
     * 根據 ID 取得單一產品
     * GET /api/products/{id}
     * 
     * RESTful 設計：
     * - 使用路徑變數 {id} 表示特定資源
     * - HTTP 200 OK: 找到資源
     * - HTTP 404 Not Found: 資源不存在
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id, 
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(product);
    }
    
    /**
     * 新增產品
     * POST /api/products
     * 
     * RESTful 設計：
     * - 使用 POST 方法新增資源
     * - HTTP 201 Created: 成功建立資源
     * - 回傳新建立的資源
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product, 
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Product createdProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    /**
     * 更新產品
     * PUT /api/products/{id}
     * 
     * RESTful 設計：
     * - 使用 PUT 方法完整更新資源
     * - HTTP 200 OK: 成功更新
     * - HTTP 404 Not Found: 資源不存在
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 設定 ID 確保更新正確的資源
        product.setProductId(id);
        Product updatedProduct = productService.saveProduct(product);
        
        return ResponseEntity.ok(updatedProduct);
    }
    
    /**
     * 部分更新產品
     * PATCH /api/products/{id}
     * 
     * RESTful 設計：
     * - 使用 PATCH 方法部分更新資源
     * - 只更新提供的欄位
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Product> partialUpdateProduct(
            @PathVariable Long id,
            @RequestBody Product productUpdates,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 只更新非 null 的欄位
        if (productUpdates.getProductName() != null) {
            existingProduct.setProductName(productUpdates.getProductName());
        }
        if (productUpdates.getUnitPrice() != null) {
            existingProduct.setUnitPrice(productUpdates.getUnitPrice());
        }
        if (productUpdates.getDescription() != null) {
            existingProduct.setDescription(productUpdates.getDescription());
        }
        if (productUpdates.getCategoryId() != null) {
            existingProduct.setCategoryId(productUpdates.getCategoryId());
        }
        if (productUpdates.getSupplierId() != null) {
            existingProduct.setSupplierId(productUpdates.getSupplierId());
        }
        
        Product updatedProduct = productService.saveProduct(existingProduct);
        return ResponseEntity.ok(updatedProduct);
    }
    
    /**
     * 刪除產品
     * DELETE /api/products/{id}
     * 
     * RESTful 設計：
     * - 使用 DELETE 方法刪除資源
     * - HTTP 204 No Content: 成功刪除，無回傳內容
     * - HTTP 404 Not Found: 資源不存在
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id, 
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根據類別 ID 取得產品列表
     * GET /api/products?categoryId={categoryId}
     * 
     * RESTful 設計：
     * - 使用查詢參數進行資源過濾
     * - 保持 URL 簡潔，篩選條件放在 query string
     */
    @GetMapping(params = "categoryId")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @RequestParam Long categoryId,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }
    
    /**
     * 檢查使用者是否已登入
     * 改用 Spring Security 的認證機制（支援 JWT）
     * @return true 如果已登入，否則 false
     */
    private boolean isAuthenticated(HttpSession session) {
        // 優先使用 Spring Security 的認證（支援 JWT）
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            return true;
        }
        
        // 備用方案：檢查 session（向後兼容）
        String username = (String) session.getAttribute("username");
        return username != null;
    }
}
