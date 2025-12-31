package dev.backend.demo.service;

import dev.backend.demo.model.User;
import dev.backend.demo.repository.UserRepository;
import dev.backend.demo.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 使用者服務類別
 * 處理使用者相關的業務邏輯
 */
@Slf4j
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    /**
     * 註冊新使用者
     * @param request 註冊請求資料
     * @return 建立的使用者物件
     * @throws IllegalArgumentException 當使用者名稱或 email 已存在時
     * @throws RuntimeException 當資料庫操作失敗時
     */
    public User register(RegisterRequest request) {
        log.info("註冊新使用者: username={}", request.getUsername());
        
        // 1. 檢查使用者名稱是否已存在
        if (userRepository.findByUsername(request.getUsername()) != null) {
            log.warn("註冊失敗: 使用者名稱已存在, username={}", request.getUsername());
            throw new IllegalArgumentException("使用者名稱已存在");
        }
        
        // 2. 檢查電子郵件是否已存在
        if (userRepository.findByEmail(request.getEmail()) != null) {
            log.warn("註冊失敗: 電子郵件已存在, email={}", request.getEmail());
            throw new IllegalArgumentException("電子郵件已存在");
        }
        
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            // 加密密碼後存入
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            User savedUser = userRepository.save(user);
            
            log.info("使用者註冊成功: userId={}, username={}", savedUser.getId(), savedUser.getUsername());
            return savedUser;
        } catch (Exception e) {
            log.error("註冊失敗: 資料庫錯誤, username={}, error={}", request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("註冊失敗，請稍後再試", e);
        }
    }
    
    /**
     * 根據使用者名稱查詢使用者
     * @param username 使用者名稱
     * @return 使用者物件，如果不存在則回傳 null
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 驗證使用者登入
     * @param username 使用者名稱
     * @param rawPassword 明文密碼
     * @return 是否驗證成功
     */
    public boolean verifyPassword(String username, String rawPassword) {
        log.debug("驗證使用者密碼: username={}", username);
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("使用者不存在: username={}", username);
            return false;
        }
        
        boolean isValid = passwordEncoder.matches(rawPassword, user.getPassword());
        if (isValid) {
            log.info("使用者密碼驗證成功: username={}", username);
        } else {
            log.warn("使用者密碼驗證失敗: username={}", username);
        }
        
        return isValid;
    }
}
