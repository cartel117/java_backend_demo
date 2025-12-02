package dev.backend.demo.dto;

/**
 * 登入請求 DTO (Data Transfer Object)
 * 用於接收前端傳來的登入資料
 */
public class LoginRequest {
    
    private String username;
    private String password;
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
