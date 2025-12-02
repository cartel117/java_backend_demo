package dev.backend.demo.service;

import dev.backend.demo.model.User;
import dev.backend.demo.repository.UserRepository;
import dev.backend.demo.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 使用者服務類別
 * 處理使用者相關的業務邏輯
 */
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
     */
    public User register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // 加密密碼後存入
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }
    
    /**
     * 驗證使用者登入
     * @param username 使用者名稱
     * @param rawPassword 明文密碼
     * @return 是否驗證成功
     */
    public boolean verifyPassword(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
