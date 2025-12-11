package dev.backend.demo.repository;

import dev.backend.demo.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // 引入 Modifying 註解，用於非查詢操作
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // 引入 Transactional 註解
import java.util.Optional;
import java.util.List;

/**
 * 購物車項目資料存取層 (Data Access Layer for CartItem)
 * 繼承 JpaRepository 介面，以獲得標準的 CRUD (增、查、改、刪) 操作能力。
 *
 * @param <CartItem>  實體類型，代表這個 Repository 操作的資料庫表格（cart_items）。
 * @param <Long>      實體主鍵 (ID) 的資料類型。
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // --- Spring Data JPA 命名查詢方法 (Derived Query Methods) ---
    // Spring Data JPA 會根據方法名稱自動生成對應的 SQL 查詢。
    
    /**
     * 查詢購物車中的特定商品 (複合唯一鍵查詢)
     *
     * 命名規則解析：
     * 1. findBy：表示這是一個查詢操作 (SELECT)。
     * 2. CartId：第一個查詢條件，根據 Cart 實體的主鍵 (ID) 進行篩選。
     * 3. And：邏輯連接詞。
     * 4. ProductId：第二個查詢條件，根據 Product 實體的主鍵 (ID) 進行篩選。
     * * @param cartId  所屬購物車的 ID
     * @param productId 產品的 ID
     * @return Optional<CartItem>  如果找到則返回項目，否則為 Optional.empty()
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    
    /**
     * 查詢屬於特定購物車的所有項目
     *
     * 命名規則解析：
     * 1. findBy：查詢操作。
     * 2. CartId：根據 Cart 實體的主鍵 (ID) 進行篩選。
     * * @param cartId 所屬購物車的 ID
     * @return List<CartItem>  該購物車下的所有項目列表
     */
    List<CartItem> findByCartId(Long cartId);
    
    /**
     * 刪除購物車的所有項目 (批量刪除)
     *
     * 由於這是一個修改資料的操作 (DELETE)，需要額外註解來通知 Spring Data JPA 處理。
     *
     * 1. @Transactional：確保整個操作在一個資料庫事務 (Transaction) 中執行。
     * 2. @Modifying：標記這是一個修改資料的查詢 (INSERT/UPDATE/DELETE)，而非 SELECT。
     * 3. deleteBy：表示這是一個刪除操作 (DELETE)。
     * 4. CartId：根據 Cart 實體的主鍵 (ID) 進行刪除。
     * * @param cartId 所屬購物車的 ID
     */
    @Transactional
    @Modifying
    void deleteByCartId(Long cartId);
    
}