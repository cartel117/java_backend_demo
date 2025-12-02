package dev.backend.demo.controller;

import dev.backend.demo.model.User;
import dev.backend.demo.service.UserService;
import dev.backend.demo.dto.RegisterRequest;
import dev.backend.demo.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 身份驗證控制器
 * 處理使用者註冊和登入相關的 HTTP 請求
 */
@RestController // RESTful API 控制器
@RequestMapping("/api/auth") // 基礎路徑為 /api/auth
public class AuthController {
    
    @Autowired // 自動注入 UserService
    private UserService userService;
    
    /**
     * 使用者註冊 API
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "註冊成功");
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "註冊失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * 使用者登入 API
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            boolean isValid = userService.verifyPassword(request.getUsername(), request.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            if (isValid) {
                response.put("success", true);
                response.put("message", "登入成功");
                response.put("username", request.getUsername());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用戶名或密碼錯誤");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "登入失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}