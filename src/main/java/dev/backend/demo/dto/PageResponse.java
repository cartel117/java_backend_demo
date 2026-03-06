package dev.backend.demo.dto;

import org.springframework.data.domain.Page;
import java.util.List;

/**
 * 分頁回應 DTO
 * 避免直接回傳 Spring Page 介面導致 Jackson 序列化失敗
 */
public class PageResponse<T> {
    
    private List<T> content;      // 當前頁的資料
    private int page;             // 當前頁碼（從 0 開始）
    private int size;             // 每頁筆數
    private long totalElements;   // 總筆數
    private int totalPages;       // 總頁數
    private boolean first;        // 是否為第一頁
    private boolean last;         // 是否為最後一頁

    public PageResponse(Page<T> pageData) {
        this.content = pageData.getContent();
        this.page = pageData.getNumber();
        this.size = pageData.getSize();
        this.totalElements = pageData.getTotalElements();
        this.totalPages = pageData.getTotalPages();
        this.first = pageData.isFirst();
        this.last = pageData.isLast();
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isFirst() { return first; }
    public boolean isLast() { return last; }
}
