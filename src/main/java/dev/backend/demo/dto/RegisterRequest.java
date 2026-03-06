package dev.backend.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 註冊請求 DTO (Data Transfer Object)
 * 用於接收前端傳來的註冊資料
 */
public class RegisterRequest {
    
    @NotBlank(message = "用戶名不能為空")
    @Size(min = 3, max = 20, message = "用戶名長度必須在 3 到 20 個字元之間")
    private String username;

    @NotBlank(message = "Email 不能為空")
    @Email(message = "Email 格式不正確")
    @Size(max = 100, message = "Email 長度不能超過 100 個字元")
    private String email;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, max = 100, message = "密碼長度必須在 6 到 100 個字元之間")
    private String password;
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
