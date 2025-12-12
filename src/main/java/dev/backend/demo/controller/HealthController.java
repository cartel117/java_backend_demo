package dev.backend.demo.controller;

import dev.backend.demo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check API 控制器
 * 
 * 用途：
 * 1. Docker 容器健康檢查
 * 2. Azure/AWS 雲端平台監控
 * 3. Load Balancer 流量分配
 * 4. 監控系統（Prometheus、Grafana）
 * 5. CI/CD 部署驗證
 */
@Slf4j
@RestController
public class HealthController {
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * 基本健康檢查
     * GET /health
     * 
     * 檢查項目：
     * - 應用程式是否能回應 HTTP 請求
     * - Spring Boot 是否正常運作
     * 
     * 用途：
     * - Docker HEALTHCHECK
     * - Load Balancer 健康檢查
     * - 快速監控
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Health check: 基本健康檢查");
        
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", Instant.now());
        status.put("application", "demo");
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * 資料庫健康檢查
     * GET /health/db
     * 
     * 檢查項目：
     * - 資料庫連線是否正常
     * - 能否執行 SQL 查詢
     * - 網路連線是否通暢
     * 
     * 用途：
     * - 深度健康檢查
     * - 監控系統告警
     * - 故障診斷
     */
    @GetMapping("/health/db")
    public ResponseEntity<Map<String, Object>> dbHealth() {
        log.debug("Health check: 資料庫健康檢查");
        
        try {
            // 執行簡單的資料庫查詢來驗證連線
            long startTime = System.currentTimeMillis();
            long count = productRepository.count();
            long responseTime = System.currentTimeMillis() - startTime;
            
            log.info("Health check: 資料庫連線正常, 回應時間={}ms, 商品數量={}", responseTime, count);
            
            Map<String, Object> status = new HashMap<>();
            status.put("database", "UP");
            status.put("responseTime", responseTime + "ms");
            status.put("timestamp", Instant.now());
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Health check: 資料庫連線失敗, error={}", e.getMessage(), e);
            
            Map<String, Object> status = new HashMap<>();
            status.put("database", "DOWN");
            status.put("error", e.getMessage());
            status.put("timestamp", Instant.now());
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(status);
        }
    }
}
