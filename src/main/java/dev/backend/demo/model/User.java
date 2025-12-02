package dev.backend.demo.model;

// Jakarta Persistence API - 用於 ORM (對象關係映射)，提供 JPA 註解如 @Entity, @Table, @Id 等
import jakarta.persistence.*;
// Lombok - 自動生成 getter/setter、toString、equals 等方法，減少樣板代碼
import lombok.Data;

/**
 * 使用者實體類別
 * 對應資料庫中的 users 表格
 */
@Data // Lombok 註解：自動生成 getter、setter、toString、equals、hashCode 方法
@Entity // JPA 註解：標記此類別為資料庫實體
@Table(name = "users") // JPA 註解：指定對應的資料庫表格名稱為 "users"
public class User {
    
    @Id // JPA 註解：標記此欄位為主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY) // JPA 註解：主鍵自動生成，使用資料庫的 AUTO_INCREMENT
    private Long id; // 使用者唯一識別碼
    
    @Column(nullable = false) // JPA 註解：設定欄位不可為 null
    private String username; // 使用者名稱
    
    @Column(nullable = false, unique = true) // JPA 註解：設定欄位不可為 null 且必須唯一
    private String email; // 使用者電子郵件（唯一）
    
    @Column(nullable = false) // JPA 註解：設定欄位不可為 null
    private String password; // 使用者密碼
}
