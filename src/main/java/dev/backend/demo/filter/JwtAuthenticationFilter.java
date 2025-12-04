package dev.backend.demo.filter;

import dev.backend.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT 身份驗證過濾器
 * 攔截每個請求，驗證 JWT token
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        logger.info("=== JWT Filter: 處理請求 " + requestURI + " ===");
        
        // 從請求頭中獲取 Authorization
        final String authorizationHeader = request.getHeader("Authorization");
        logger.info("Authorization header: " + (authorizationHeader != null ? authorizationHeader.substring(0, Math.min(30, authorizationHeader.length())) + "..." : "null"));

        String username = null;
        String jwt = null;

        // 檢查 Authorization 頭是否存在且以 "Bearer " 開頭
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // 提取 token（去除 "Bearer " 前綴）
            logger.info("提取到 JWT token，長度: " + jwt.length());
            try {
                username = jwtUtil.extractUsername(jwt); // 從 token 中提取使用者名稱
                logger.info("從 token 提取到使用者名稱: " + username);
            } catch (Exception e) {
                // token 解析失敗
                logger.error("無法解析 JWT token: " + e.getMessage(), e);
            }
        } else {
            logger.warn("Authorization header 不存在或格式錯誤");
        }

        // 如果成功提取使用者名稱且當前沒有已認證的使用者
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("開始驗證 token...");
            // 驗證 token
            if (jwtUtil.validateToken(jwt, username)) {
                logger.info("✅ Token 驗證成功，使用者: " + username);
                // 建立認證對象
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 將認證對象設定到 Spring Security 上下文中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                logger.error("❌ Token 驗證失敗");
            }
        } else if (username == null) {
            logger.warn("❌ 未能從 token 提取使用者名稱");
        }
        
        // 繼續執行過濾鏈
        filterChain.doFilter(request, response);
    }
}
