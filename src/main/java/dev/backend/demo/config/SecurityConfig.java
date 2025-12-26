package dev.backend.demo.config;

import dev.backend.demo.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置
 * 配置哪些路徑需要認證，哪些可以公開訪問
 */
@Configuration // 標記這個類是一個配置類，會被 Spring 容器掃描
@EnableWebSecurity // 啟用 Spring Security 的 Web 安全功能
public class SecurityConfig {

    @Autowired
    // 注入自訂的 JWT 認證過濾器，用於從請求中解析和驗證 JWT
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置 HTTP 安全規則
     * 這是 Spring Security 6 之後主要的配置方法，用於定義安全過濾鏈。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 關閉 CSRF 保護：
            // 由於使用的是 JWT 和 RESTful API，通常不需要基於 Session 的 CSRF 保護。
            // (開發階段，生產環境建議審慎評估是否需要開啟或使用其他方式防護)
            .csrf(csrf -> csrf.disable()) 
            // 2. 啟用 CORS 配置：
            // 允許跨來源資源共享，通常與 Spring MVC 的 CORS 配置一起使用。
            .cors(cors -> {}) 
            // 3. 配置授權規則
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/login",
                    "/login.html",
                    "/register",
                    "/register.html",
                    "/shop.html",           // 允許訪問購物頁面
                    "/products.html",       // 允許訪問管理頁面
                    "/test-storage.html",   // 允許測試頁面
                    "/api/auth/**",         // 允許訪問認證 API
                    "/health",              // 允許健康檢查端點
                    "/health/**",           // 允許所有健康檢查相關端點
                    "/static/**",           // 允許所有靜態資源
                    "/*.css",               // 允許所有 CSS 檔案
                    "/*.js",                // 允許所有 JS 檔案
                    "/*.html",              // 允許所有 HTML 檔案
                    "/app.js",              // 明確允許 app.js
                    "/styles.css",          // 明確允許 styles.css
                    "/favicon.ico"          // 允許網站圖示
                ).permitAll() // 允許所有人訪問這些路徑
                .anyRequest().authenticated() // 其他所有請求需要認證（JWT Filter 會處理）
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 設定為無狀態 session
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 添加 JWT 過濾器
            .formLogin(form -> form.disable()) // 禁用默認的表單登入
            .httpBasic(basic -> basic.disable()); // 禁用 HTTP Basic 認證

        return http.build();
    }

    /**
     * 密碼加密器 Bean
     * 使用 BCrypt 演算法加密密碼
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
