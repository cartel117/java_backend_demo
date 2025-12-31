package dev.backend.demo.exception;

/**
 * 使用者名稱重複異常
 * 當註冊時使用者名稱已存在時拋出此異常
 */
public class DuplicateUsernameException extends RuntimeException {
    
    public DuplicateUsernameException(String message) {
        super(message);
    }
    
    public DuplicateUsernameException(String username, String detail) {
        super("使用者名稱已存在: " + username + " - " + detail);
    }
}
