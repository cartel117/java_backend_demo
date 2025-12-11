package dev.backend.demo.repository;

import dev.backend.demo.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 購物車資料存取層 (Data Access Layer for Cart)
 * 繼承 JpaRepository 介面，以獲得標準的 CRUD (增、查、改、刪) 操作能力。
 *
 * @param <Cart>  實體類型，代表這個 Repository 操作的資料庫表格（carts）。
 * @param <Long>  實體主鍵 (ID) 的資料類型。
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    // --- Spring Data JPA 命名查詢方法 (Derived Query Methods) ---
    
    /**
     * 根據使用者 ID 查詢購物車
     *
     * 命名規則解析：findBy + 實體屬性名稱 (UserId)。
     * Spring Data JPA 會自動生成 SQL: SELECT * FROM carts WHERE user_id = ?
     * * @param userId 欲查詢的使用者 ID
     * @return Optional<Cart>  如果找到則返回購物車，否則為 Optional.empty()
     */
    Optional<Cart> findByUserId(Long userId);
    
    /**
     * 查詢購物車及其所有項目 (Eager Fetching 避免 N+1 問題)
     *
     * ⚠️ **手動 HQL/JPQL 查詢:**
     * 因為標準的命名查詢無法處理複雜的 JOIN FETCH，這裡使用 @Query 註解，
     * 直接編寫 HQL (Hibernate Query Language) 或 JPQL (Java Persistence Query Language)。
     *
     * 關鍵詞：
     * 1. `LEFT JOIN FETCH c.items`: 這是一個**提取連接 (FETCH JOIN)**。
     * - 它不僅將 `Cart` 和 `CartItem` (c.items) 連接起來，而且保證在執行單一查詢時，
     * 立即將所有的 `CartItem` 資料**載入**到 `Cart` 物件的 `items` 列表中。
     * - 這樣做可以覆蓋 `Cart` 實體中為 `items` 設置的 `FetchType.LAZY` (懶載入) 設定，
     * 避免在後續存取 `items` 列表時產生多個資料庫查詢 (N+1 問題)。
     * 2. `WHERE c.userId = :userId`: 查詢條件，`:userId` 是方法參數的命名參數。
     *
     * * @param userId 欲查詢的使用者 ID
     * @return Optional<Cart>  包含已載入所有 CartItem 的購物車
     */
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.userId = :userId")
    Optional<Cart> findByUserIdWithItems(Long userId);
    
    /**
     * 檢查使用者是否有購物車 (存在性查詢)
     *
     * 命名規則解析：existsBy + 實體屬性名稱 (UserId)。
     * Spring Data JPA 會生成優化過的 SQL，通常是 SELECT 1 LIMIT 1，以判斷是否存在記錄，
     * 效率比先查詢再判斷是否為空要高。
     * * @param userId 欲檢查的使用者 ID
     * @return boolean  如果存在則返回 true，否則返回 false
     */
    boolean existsByUserId(Long userId);
}