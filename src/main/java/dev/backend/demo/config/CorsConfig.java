package dev.backend.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS (Cross-Origin Resource Sharing) 跨域配置
 * 
 * 為什麼需要 CORS？
 * 瀏覽器的「同源政策」會阻止不同網域之間的請求，例如：
 * - 前端: http://localhost:3000
 * - 後端: http://localhost:8080
 * 這兩個不同 port，瀏覽器會認為是不同來源而阻擋請求
 * 
 * 設定 CORS 後，可以允許前端跨域存取後端 API
 */
@Configuration // 標記為配置類別
public class CorsConfig {

    /**
     * 建立 CORS 過濾器
     * 這個過濾器會在每個 HTTP 請求進來時檢查是否允許跨域
     */
    @Bean
    public CorsFilter corsFilter() {
        // 1. 建立 CORS 配置來源，用來註冊哪些路徑需要套用 CORS 規則
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // 2. 建立 CORS 配置物件，設定具體的跨域規則
        CorsConfiguration config = new CorsConfiguration();
        
        // 3. 允許所有來源（開發環境設定）
        //    "*" = 任何網域都可以存取，例如: http://localhost:3000, https://example.com
        //    ⚠️ 生產環境建議改成具體網域: config.addAllowedOrigin("https://your-domain.com")
        config.addAllowedOriginPattern("*");
        
        // 4. 允許所有 HTTP 方法
        //    "*" = GET, POST, PUT, DELETE, PATCH 等所有方法都允許
        config.addAllowedMethod("*");
        
        // 5. 允許所有請求標頭
        //    "*" = Content-Type, Authorization 等所有標頭都允許
        //    前端可以在請求中帶任何自訂標頭
        config.addAllowedHeader("*");
        
        // 6. 允許發送認證資訊（Cookie、HTTP 認證）
        //    true = 前端可以在跨域請求中帶 Cookie 或 Authorization token
        //    ⚠️ 如果設為 true 且用於生產環境，不能使用 "*" 作為 origin，必須指定具體網域
        config.setAllowCredentials(true);
        
        // 7. 註冊 CORS 配置到所有路徑
        //    "/**" = 對所有 API 路徑都套用此 CORS 規則
        source.registerCorsConfiguration("/**", config);
        
        // 8. 建立並回傳 CORS 過濾器，Spring 會自動將此過濾器加入請求處理鏈中
        return new CorsFilter(source);
    }
}
