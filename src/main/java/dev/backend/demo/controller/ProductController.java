package dev.backend.demo.controller;

import dev.backend.demo.model.Product;
import dev.backend.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 產品控制器
 * 處理產品相關的 HTTP 請求
 */
@RestController  // 改用 @RestController，回傳 JSON
@RequestMapping("/api")  // API 基礎路徑
public class ProductController {
    
    // 注入 ProductService（需要建立）
    @Autowired
    private ProductService productService;
    
    /**
     * 取得產品列表 API
     * GET /api/products
     * 
     * @param session HttpSession 物件，用來取得使用者登入狀態
     * @return JSON 格式的回應
     * {
     *   "success": true,
     *   "products": [...]
     * }
     */
    @GetMapping("/products")
    public ResponseEntity<?> getProducts(HttpSession session) {
        // 1. 建立回應 Map
        Map<String, Object> response = new HashMap<>();
        
        // 2. 從 session 中取得使用者名稱
        String username = (String) session.getAttribute("username");
        
        // 3. 檢查使用者是否已登入
        if (username == null) {
            // 未登入 → 回傳 401 錯誤
            response.put("success", false);
            response.put("message", "請先登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            // 4. 從資料庫取得所有產品
            List<Product> products = productService.getAllProducts();
            
            // 5. 建立成功的回應
            response.put("success", true);
            response.put("products", products);  // 產品列表
            
            // 6. 回傳 HTTP 200 + JSON
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // 7. 發生錯誤 → 回傳 500 錯誤
            response.put("success", false);
            response.put("message", "取得產品列表失敗");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
