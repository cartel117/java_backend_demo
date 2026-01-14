package dev.backend.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理員控制器
 * 演示如何使用 @PreAuthorize 進行權限控制
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理員 API", description = "僅限 ADMIN 角色訪問")
public class AdminController {
    
    /**
     * 僅 ADMIN 角色可以訪問的 API
     * 使用 @PreAuthorize 註解進行權限檢查
     * 權限從資料庫動態查詢，而非從 Token 讀取
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "管理員儀表板",
        description = "僅限 ADMIN 角色訪問。權限從資料庫動態查詢。"
    )
    public ResponseEntity<?> getDashboard() {
        log.info("API: 管理員訪問儀表板");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "歡迎，管理員！");
        response.put("data", Map.of(
            "totalUsers", 100,
            "totalOrders", 500,
            "revenue", 150000
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ADMIN 和 CUSTOMER 都可以訪問的 API
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Operation(
        summary = "統計資訊",
        description = "ADMIN 和 CUSTOMER 都可以訪問"
    )
    public ResponseEntity<?> getStats() {
        log.info("API: 用戶訪問統計資訊");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "統計資訊");
        response.put("data", Map.of("visitors", 1000));
        
        return ResponseEntity.ok(response);
    }
}
