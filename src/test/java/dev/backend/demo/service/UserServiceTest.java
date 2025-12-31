package dev.backend.demo.service;

import dev.backend.demo.dto.RegisterRequest;
import dev.backend.demo.model.User;
import dev.backend.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserService 單元測試
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    /**
     * 測試：成功註冊新使用者
     */
    @Test
    void testRegisterSuccess() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // Act
        User result = userService.register(request);
        
        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    /**
     * 測試：使用者名稱已存在
     */
    @Test
    void testRegisterDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        when(userRepository.findByUsername("existinguser")).thenReturn(existingUser);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(request);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    /**
     * 測試：Email 已存在
     */
    @Test
    void testRegisterDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        
        when(userRepository.findByUsername("newuser")).thenReturn(null);
        
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        when(userRepository.findByEmail("existing@example.com")).thenReturn(existingUser);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(request);
        });
    }
    
    /**
     * 測試：根據使用者名稱查詢
     */
    @Test
    void testFindByUsername() {
        User testUser = new User();
        testUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        
        User result = userService.findByUsername("testuser");
        
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }
}
