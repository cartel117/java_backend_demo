package dev.backend.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置屬性
 * 從 application.properties 讀取 jwt.* 開頭的配置
 * 
 * 使用方式：
 * 1. 開發環境：使用 application.properties 的預設值
 * 2. 生產環境：設定環境變數 JWT_SECRET 和 JWT_EXPIRATION
 * 
 * 範例：
 * export JWT_SECRET=your-production-secret-key
 * export JWT_EXPIRATION=86400000
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    /**
     * JWT 密鑰（用於簽署和驗證 token）
     * 生產環境應使用環境變數，至少 256 位元（32 字元以上）
     */
    private String secret;
    
    /**
     * JWT 過期時間（毫秒）
     * 預設：86400000ms = 24 小時
     */
    private Long expiration = 86400000L;
    
    // Getters and Setters
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public Long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
}
