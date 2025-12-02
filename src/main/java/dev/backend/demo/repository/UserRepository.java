package dev.backend.demo.repository;

import dev.backend.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 使用者資料存取層
 * 繼承 JpaRepository 自動提供 CRUD 操作
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根據使用者名稱查詢使用者
     * Spring Data JPA 會自動實作此方法
     */
    User findByUsername(String username);
    
    /**
     * 根據電子郵件查詢使用者
     */
    User findByEmail(String email);
}
