package dev.backend.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 頁面路由控制器（選用，用於靜態前端網頁）
 *
 * ⚠️ 注意：此控制器為「選用」功能，未來可能用不到
 * 
 * 用途：
 * - 此控制器僅負責前端靜態頁面路由的轉發（forward），不進行任何資料處理。
 * - 提供簡潔的網址（例如 `/login` 而非 `/login.html`），改善使用者體驗。
 * - 使用 forward 將請求導向到 `src/main/resources/static` 中的對應 HTML 檔案。
 * - 與 Spring Security 配合：這些路由已在 `SecurityConfig` 中設定為 permitAll，
 *   因此可匿名訪問。
 *
 * 何時可以移除：
 * - 如果前端改用單頁應用（SPA）框架（如 React、Vue）並自行處理路由
 * - 如果不介意使用者直接訪問 `/login.html`、`/register.html` 等完整檔名
 * - 如果未來改用伺服器端渲染（SSR）或模板引擎（如 Thymeleaf）
 * 
 * 路由一覽：
 * - GET /           -> forward:/index.html（首頁）
 * - GET /login      -> forward:/login.html（登入頁）
 * - GET /register   -> forward:/register.html（註冊頁）
 */
@Controller
public class PageController {
    
    @GetMapping("/")
    /**
     * 首頁路由
     * 將根路徑 `/` 轉發到靜態頁面 `index.html`。
     * 使用 forward 而非 redirect，可保留原始請求路徑並由靜態資源直接回應。
     */
    public String index() {
        return "forward:/index.html";
    }
    
    @GetMapping("/login")
    /**
     * 登入頁路由
     * 轉發 `/login` 到 `login.html`，供使用者進行登入操作。
     */
    public String login() {
        return "forward:/login.html";
    }
    
    @GetMapping("/register")
    /**
     * 註冊頁路由
     * 轉發 `/register` 到 `register.html`，供使用者進行註冊操作。
     */
    public String register() {
        return "forward:/register.html";
    }
}
