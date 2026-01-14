package dev.backend.demo.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 角色實體類別
 * 對應資料庫中的 roles 表格
 */
@Data
@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String name; // 角色名稱：ADMIN, CUSTOMER
}
