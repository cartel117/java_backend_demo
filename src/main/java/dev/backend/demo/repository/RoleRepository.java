package dev.backend.demo.repository;

import dev.backend.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 角色資料訪問介面
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根據角色名稱查詢角色
     */
    Role findByName(String name);
}
