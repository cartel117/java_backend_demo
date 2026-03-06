package dev.backend.demo.controller;

import dev.backend.demo.dto.PageResponse;
import dev.backend.demo.model.Product;
import dev.backend.demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 產品控制器 (RESTful API)
 * 
 * RESTful API 設計原則：
 * - 使用 HTTP 方法表達操作意圖：GET(查詢)、POST(新增)、PUT(更新)、DELETE(刪除)
 * - URL 代表資源，使用名詞而非動詞
 * - 使用標準 HTTP 狀態碼
 * - 資源的 ID 放在 URL 路徑中
 * 
 * 認證機制：
 * - 所有端點都需要 JWT 認證（由 SecurityConfig 統一配置）
 * - JwtAuthenticationFilter 會自動驗證 Token 並設定 SecurityContext
 * - 如果認證失敗，Spring Security 會自動返回 401 Unauthorized
 */
@RestController
@RequestMapping("/api/products")  // RESTful: 資源為複數名詞
// @Tag: 在 Swagger UI 中將這個 Controller 的所有 API 分組到「產品 API」標籤下
// 方便在文檔中分類查看，提供標籤名稱和描述
@Tag(name = "產品 API", description = "產品管理相關 API（需要 JWT 認證）")
// @SecurityRequirement: 標記這個 Controller 的所有 API 都需要 JWT 認證
// 在 Swagger UI 中會顯示 🔒 鎖頭圖示，提醒使用者需要先登入取得 Token
@SecurityRequirement(name = "Bearer Authentication")
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
     * 
     * 認證處理：
     * - SecurityConfig 已配置 /api/products/** 需要認證
     * - 如果沒有有效的 JWT Token，請求會在 Filter 層被攔截
     * - Authentication 參數可選，用於需要取得當前使用者資訊時
     */
    @GetMapping
    @Operation(
        summary = "查詢產品列表（支援分頁）",
        description = "取得產品列表，支援分頁與排序。可選擇性根據分類 ID 篩選。需要 JWT Token 認證。"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "查詢成功",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "content": [
                        {
                          "id": 1,
                          "name": "iPhone 15 Pro",
                          "price": 35900.00,
                          "categoryId": 1,
                          "imageUrl": "https://example.com/iphone15.jpg",
                          "description": "最新款 iPhone"
                        }
                      ],
                      "totalElements": 100,
                      "totalPages": 10,
                      "number": 0,
                      "size": 10,
                      "first": true,
                      "last": false
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "401", description = "未認證（需要 JWT Token）")
    })
    public ResponseEntity<PageResponse<Product>> getAllProducts(
            @Parameter(description = "頁碼（從 0 開始）", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁筆數", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序欄位", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向：asc 或 desc", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "分類 ID（可選）")
            @RequestParam(required = false) Long categoryId,
            Authentication authentication) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = categoryId != null
                ? productService.getProductsByCategoryIdPaged(categoryId, pageable)
                : productService.getAllProductsPaged(pageable);

        return ResponseEntity.ok(new PageResponse<>(products));
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
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
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
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
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
            @RequestBody Product product) {
        
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
            @RequestBody Product productUpdates) {
        
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
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
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
    public ResponseEntity<List<Product>> getProductsByCategory(@RequestParam Long categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }
    
}
