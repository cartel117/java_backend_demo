package dev.backend.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * ----------------------------------------------------
 * 🛡️ CORS (Cross-Origin Resource Sharing) 跨域配置 🛡️
 * ----------------------------------------------------
 * * 為什麼需要 CORS？
 * 瀏覽器的「同源政策」（Same-Origin Policy）會阻止不同「來源」（協議、網域、埠號）之間的請求。
 * 例如：
 * - 前端: http://localhost:3000 (來源 A)
 * - 後端: http://localhost:8080 (來源 B)
 * 這兩個不同 port，瀏覽器會認為是不同來源 (Origin) 而阻擋請求。
 * 設定 CORS 後，後端會在 HTTP 響應頭中明確告訴瀏覽器：「允許來源 A 存取我的資源」。
 * * 配置方式：使用 CorsFilter 確保在請求進入 Spring DispatcherServlet 之前就處理 CORS 邏輯。
 */
@Configuration // 標記為配置類別，Spring Boot 啟動時會讀取並執行這個類別
public class CorsConfig {

    /**
     * 註冊一個 CorsFilter Bean。
     * 這個過濾器會被加入到 Spring Security (如果使用) 或 Servlet Filter Chain 中，
     * 負責在每個 HTTP 請求進來時檢查並添加 CORS 相關的響應頭。
     */
    @Bean
    public CorsFilter corsFilter() {
        // 1. 建立 CORS 配置來源 (Source)
        // 用來註冊多個路徑 (e.g., /api/*, /admin/*) 及其各自應用的 CORS 規則
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // 2. 建立 CORS 配置物件 (Configuration)
        // 定義了一套具體的跨域規則
        CorsConfiguration config = new CorsConfiguration();
        
        // 3. 允許所有來源 (Allowed Origin)
        // config.addAllowedOriginPattern("*") 設置為 * 表示允許任何網域 (http://localhost:3000, https://example.com) 存取。
        // 🚨 安全警告：生產環境中，應將此行替換為具體的網域，以避免不必要的安全風險。
        config.addAllowedOriginPattern("*"); 
        
        // 4. 允許發送認證資訊 (Credentials)
        // true = 允許前端在跨域請求中攜帶 Cookie 或 Authorization token。
        // ⚠️ 強制規則：如果此處設為 true，則步驟 3 的 Origin 絕對不能是 "*" (必須指定具體網域)。
        // Spring CorsFilter 會自動將響應頭 Access-Control-Allow-Origin 設置為實際的請求 Origin。
        config.setAllowCredentials(true); 
        
        // 5. 允許所有 HTTP 方法 (Allowed Methods)
        // "*" = GET, POST, PUT, DELETE, PATCH, OPTIONS 等所有方法都允許進行跨域訪問。
        // OPTIONS 是瀏覽器發送預檢請求 (Preflight Request) 時使用的方法。
        config.addAllowedMethod("*");
        
        // 6. 允許所有請求標頭 (Allowed Headers)
        // "*" = 允許前端在請求中帶任何自定義或標準標頭，如 Content-Type, Authorization, X-Request-With 等。
        config.addAllowedHeader("*");
        
        // 7. 設置預檢請求 (Preflight Request) 的緩存時間
        // 瀏覽器會在 maxAge 秒數內緩存 OPTIONS 預檢請求的結果，減少重複發送 OPTIONS 請求的次數。
        // 單位為秒 (seconds)。
        config.setMaxAge(3600L); // 緩存 1 小時

        // 8. 註冊 CORS 配置到所有 API 路徑
        // "/**" 表示此 config 規則適用於應用程式中的所有路徑。
        source.registerCorsConfiguration("/**", config);
        
        // 9. 建立並回傳 CORS 過濾器
        return new CorsFilter(source);
    }
}