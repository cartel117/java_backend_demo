package dev.backend.demo.controller;

import dev.backend.demo.dto.LoginRequest;
import dev.backend.demo.dto.RegisterRequest;
import dev.backend.demo.model.User;
import dev.backend.demo.service.UserService;
import dev.backend.demo.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuthController 單元測試
 * 使用 Mockito 模擬依賴的服務，測試控制器的業務邏輯
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 測試")
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private User mockUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // 準備測試資料
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    // ==================== 註冊測試 ====================

    @Test
    @DisplayName("註冊成功")
    void testRegister_Success() {
        // Arrange
        when(userService.register(any(RegisterRequest.class))).thenReturn(mockUser);

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("註冊成功", body.get("message"));
        assertEquals("testuser", body.get("username"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("註冊失敗 - 使用者名稱已存在")
    void testRegister_DuplicateUsername() {
        // Arrange
        when(userService.register(any(RegisterRequest.class)))
            .thenThrow(new IllegalArgumentException("註冊失敗：用戶名已存在"));

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode()); // 409 - 資源衝突
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("註冊失敗：用戶名已存在", body.get("message"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("註冊失敗 - Email 已存在")
    void testRegister_DuplicateEmail() {
        // Arrange
        when(userService.register(any(RegisterRequest.class)))
            .thenThrow(new IllegalArgumentException("註冊失敗：Email 已存在"));

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode()); // 409 - 資源衝突
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("註冊失敗：Email 已存在", body.get("message"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("註冊失敗 - 系統錯誤")
    void testRegister_SystemError() {
        // Arrange
        when(userService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("資料庫連線失敗"));

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("系統錯誤，請稍後再試", body.get("message"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    // ==================== 登入測試 ====================

    @Test
    @DisplayName("登入成功")
    void testLogin_Success() {
        // Arrange
        String mockToken = "mock.jwt.token";
        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        when(userService.verifyPassword("testuser", "password123")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", 1L)).thenReturn(mockToken);

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("登入成功", body.get("message"));
        assertEquals("testuser", body.get("username"));
        assertEquals(1L, body.get("userId"));
        assertEquals(mockToken, body.get("token"));

        verify(userService, times(1)).findByUsername("testuser");
        verify(userService, times(1)).verifyPassword("testuser", "password123");
        verify(jwtUtil, times(1)).generateToken("testuser", 1L);
    }

    @Test
    @DisplayName("登入失敗 - 使用者不存在")
    void testLogin_UserNotFound() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(null);

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("用戶名或密碼錯誤", body.get("message"));

        verify(userService, times(1)).findByUsername("testuser");
        verify(userService, never()).verifyPassword(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("登入失敗 - 密碼錯誤")
    void testLogin_WrongPassword() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        when(userService.verifyPassword("testuser", "password123")).thenReturn(false);

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("用戶名或密碼錯誤", body.get("message"));

        verify(userService, times(1)).findByUsername("testuser");
        verify(userService, times(1)).verifyPassword("testuser", "password123");
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("登入失敗 - 系統異常")
    void testLogin_SystemException() {
        // Arrange
        when(userService.findByUsername("testuser"))
            .thenThrow(new RuntimeException("資料庫連線失敗"));

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("登入失敗"));

        verify(userService, times(1)).findByUsername("testuser");
    }

    // ==================== 登出測試 ====================

    @Test
    @DisplayName("登出成功")
    void testLogout_Success() {
        // Act
        ResponseEntity<?> response = authController.logout();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("登出成功", body.get("message"));
    }

    // ==================== 邊界條件測試 ====================

    @Test
    @DisplayName("註冊 - 空白使用者名稱")
    void testRegister_EmptyUsername() {
        // Arrange
        registerRequest.setUsername("");
        when(userService.register(any(RegisterRequest.class)))
            .thenThrow(new IllegalArgumentException("使用者名稱不能為空"));

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
    }

    @Test
    @DisplayName("登入 - 空白密碼")
    void testLogin_EmptyPassword() {
        // Arrange
        loginRequest.setPassword("");
        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        when(userService.verifyPassword("testuser", "")).thenReturn(false);

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
    }
}
