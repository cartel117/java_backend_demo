package dev.backend.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger 配置類
 * 
 * 用途：
 * - 自動生成 API 文檔（Swagger UI）
 * - 提供 RESTful API 規格說明（OpenAPI 3.0 標準）
 * - 支援 JWT 認證的 API 測試介面
 * 
 * 訪問路徑：
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/api-docs
 * 
 * 為什麼需要 OpenAPI？
 * - 前後端分離開發時，後端提供標準 API 文檔給前端參考
 * - 可直接在網頁上測試 API，不需要使用 Postman 等工具
 * - 自動產生，不需要手寫文檔，減少維護成本
 * - 支援多種程式語言的 Client SDK 自動生成
 * 
 * 如何在 Swagger UI 中測試需要認證的 API？
 * 1. 先呼叫 /api/auth/login 取得 JWT Token
 * 2. 點擊右上角的 🔓 Authorize 按鈕
 * 3. 在彈出視窗輸入 Token（不需要加 Bearer 前綴）
 * 4. 點擊 Authorize，之後的 API 測試就會自動帶入 Token
 */
@Configuration // 標記這是 Spring 的配置類
public class OpenApiConfig {

    /**
     * 配置 OpenAPI 規格
     * 
     * @Bean 註解告訴 Spring 這是一個 Bean 方法，會被 Spring 容器管理
     * 這個方法回傳的 OpenAPI 物件會被 springdoc-openapi 使用來生成 API 文檔
     * 
     * 配置項目：
     * 1. SecurityScheme：定義 API 使用的認證方式（JWT Bearer Token）
     * 2. SecurityRequirement：設定哪些 API 需要認證
     * 3. Info：API 的基本資訊（標題、版本、描述、聯絡人）
     * 4. Servers：定義 API 伺服器位址（本地開發 & Azure 生產環境）
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 1. 定義 JWT 認證方式
        // type = HTTP 表示使用標準 HTTP 認證
        // scheme = bearer 表示使用 Bearer Token 格式
        // bearerFormat = JWT 說明 Token 是 JWT 格式
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)      // 使用 HTTP 認證
                .scheme("bearer")                     // Bearer Token 方式
                .bearerFormat("JWT")                  // Token 格式為 JWT
                .in(SecurityScheme.In.HEADER)         // Token 放在 HTTP Header 中
                .name("Authorization");               // Header 的名稱

        // 2. 將認證方式套用到所有需要認證的 API
        // 這會讓 @SecurityRequirement(name = "Bearer Authentication") 的 API 都需要 JWT Token
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        // 3. 回傳完整的 OpenAPI 配置
        return new OpenAPI()
                // 設定 API 的基本資訊
                .info(new Info()
                        .title("電商系統後端 API")
                        .description("""
                                完整的電商後端 API，包含：
                                - 用戶註冊與登入（JWT 認證）
                                - 產品管理（CRUD 操作）
                                - 購物車功能
                                - 健康檢查
                                
                                ## 如何使用
                                1. 先呼叫 `/api/auth/register` 註冊帳號
                                2. 呼叫 `/api/auth/login` 登入取得 JWT Token
                                3. 點擊右上角 🔓 Authorize 按鈕
                                4. 輸入 Token（不需要加 Bearer 前綴）
                                5. 即可測試需要認證的 API
                                
                                測試帳號：username=cartel117, password=password123
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Cartel Su")
                                .email("cartelsu@walton.com.tw")
                                .url("https://github.com/cartel117"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // 設定 API 伺服器（本地 & Azure）
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("本地開發環境"),
                        new Server()
                                .url("https://demo-springapp-20251226.azurewebsites.net")
                                .description("Azure 生產環境")
                ))
                // 將 JWT 認證套用到整個 API
                .addSecurityItem(securityRequirement)
                .schemaRequirement("Bearer Authentication", securityScheme);
    }
}
