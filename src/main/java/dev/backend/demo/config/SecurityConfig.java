package dev.backend.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置
 * 配置哪些路徑需要認證，哪些可以公開訪問
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                    "/api/auth/**",
                    "/static/**",
                    "/*.css",
                    "/*.js",
                    "/*.html"
                ).permitAll() // 允許所有人訪問這些路徑
                .anyRequest().authenticated() // 其他所有請求需要認證
            )
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
