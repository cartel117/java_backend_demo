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
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置 HTTP 安全規則
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 關閉 CSRF 保護（開發階段，生產環境建議啟用）
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
