package dev.backend.demo.controller;

import dev.backend.demo.model.User;
import dev.backend.demo.service.UserService;
import dev.backend.demo.dto.RegisterRequest;
import dev.backend.demo.dto.LoginRequest;
import dev.backend.demo.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController // RESTful API 控制器
@RequestMapping("/api/auth") // 基礎路徑為 /api/auth
@Tag(name = "認證 API", description = "用戶註冊與登入相關 API")
public class AuthController {
    
    @Autowired // 自動注入 UserService
    private UserService userService;

    @Autowired // 自動注入 JwtUtil
    private JwtUtil jwtUtil;
    
    /**
     * 使用者註冊 API
     * POST /api/auth/register
     */
    @PostMapping("/register")
    //summary: 這是 API 的「標題」，通常簡短地說明這個 API 是做什麼的。
    //description: 這是 API 的「詳細說明」。您可以在這裡補充業務邏輯、注意事項或背後執行的細節。
    // === [Swagger Documentation Start] ===
    @Operation(
        summary = "用戶註冊",
        description = "註冊新用戶帳號。用戶名和 Email 必須唯一，密碼會使用 BCrypt 加密儲存。"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "註冊成功",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "註冊成功",
                      "username": "cartel117"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "註冊失敗（用戶名或 Email 已存在）",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "註冊失敗：用戶名已存在"
                    }
                    """)
            )
        )
    })
    // === [Swagger Documentation End] ===
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        log.info("API: 使用者註冊請求, username={}", request.getUsername());
        
        try {
            User user = userService.register(request);
            log.info("API: 使用者註冊成功, username={}", user.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "註冊成功");
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("API: 使用者註冊失敗 - 業務邏輯錯誤, username={}, error={}", 
                     request.getUsername(), e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            // 🔍 根據錯誤訊息判斷使用更精確的狀態碼
            HttpStatus status = e.getMessage().contains("已存在") 
                ? HttpStatus.CONFLICT  // 409 - 資源衝突
                : HttpStatus.BAD_REQUEST;  // 400 - 其他輸入錯誤
            
            return ResponseEntity.status(status).body(response);
            
        } catch (RuntimeException e) {
            // 系統錯誤（資料庫連線問題等）
            log.error("API: 使用者註冊失敗 - 系統錯誤, username={}, error={}", request.getUsername(), e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "系統錯誤，請稍後再試");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 使用者登入 API
     * POST /api/auth/login
     */
    @PostMapping("/login")//設定這個方法處理對 "/login" 路徑的 HTTP POST 請求
    @Operation(
        summary = "用戶登入",
        description = "使用用戶名和密碼登入，成功後返回 JWT Token。Token 有效期為 24 小時。"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "登入成功",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "登入成功",
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "user": {
                        "id": 1,
                        "username": "cartel117",
                        "email": "cartel@example.com"
                      }
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "登入失敗（用戶名或密碼錯誤）",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "用戶名或密碼錯誤"
                    }
                    """)
            )
        )
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // @RequestBody 註解告訴 Spring 將請求 Body (通常是 JSON) 自動轉換為 LoginRequest 物件
        log.info("API: 使用者登入請求, username={}", request.getUsername());
        
        try {
            // 先取得使用者資訊
            // 嘗試通過使用者名稱從資料庫或其他儲存庫中查找使用者
            User user = userService.findByUsername(request.getUsername());
            
            if (user == null) {
                //如果使用者不存在，返回 UNAUTHORIZED 錯誤，但不透露是使用者名稱還是密碼錯誤
                log.warn("API: 登入失敗 - 使用者不存在, username={}", request.getUsername());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用戶名或密碼錯誤");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            // 驗證密碼。userService 內部會處理密碼的雜湊比對 (e.g., 使用 BCrypt)
            boolean isValid = userService.verifyPassword(request.getUsername(), request.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            if (isValid) {
                // ✅ 登入成功，生成包含 userId 的 JWT token
                String token = jwtUtil.generateToken(user.getUsername(), user.getId());
                
                log.info("API: 使用者登入成功, username={}, userId={}", user.getUsername(), user.getId());
                
                response.put("success", true);
                response.put("message", "登入成功");
                response.put("username", user.getUsername());
                response.put("userId", user.getId());
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                //密碼驗證失敗，同樣返回 UNAUTHORIZED，並使用與使用者不存在時相同的訊息，以防洩露資訊
                log.warn("API: 登入失敗 - 密碼錯誤, username={}", request.getUsername());
                
                response.put("success", false);
                response.put("message", "用戶名或密碼錯誤");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            //處理在登入過程中發生的任何意外錯誤 (例如資料庫連線問題、服務層異常)
            log.error("API: 登入發生異常, username={}, error={}", request.getUsername(), e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "登入失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 使用者登出 API
     * POST /api/auth/logout
     * 注意：使用 JWT 時，登出通常由客戶端處理（刪除 token）
     * 伺服器端可以選擇實現 token 黑名單機制
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登出成功");
        return ResponseEntity.ok(response);
    }
}