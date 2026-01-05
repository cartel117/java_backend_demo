# ============================================
# Multi-stage Docker Build for Spring Boot Application
# ============================================
# 
# Stage 1: Build stage - 編譯應用程式
# Stage 2: Runtime stage - 運行應用程式
# 
# 好處：
# 1. 減少映像檔大小（不包含 Maven 等建置工具）
# 2. 提升安全性（只包含運行時必需的檔案）
# 3. 加速部署（較小的映像檔傳輸更快）
# ============================================

# ============================================
# Stage 1: Build
# ============================================
FROM eclipse-temurin:21-jdk-alpine AS builder

# 設定工作目錄
WORKDIR /app

# 複製 Maven 設定檔和原始碼
# 優化：先複製 pom.xml，利用 Docker 層快取加速建置
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 授予 mvnw 執行權限
RUN chmod +x mvnw

# 下載相依套件（此層會被快取，除非 pom.xml 改變）
RUN ./mvnw dependency:go-offline

# 複製原始碼
COPY src ./src

# 編譯並打包應用程式（跳過測試以加速建置）
RUN ./mvnw clean package -DskipTests

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:21-jre-alpine

# 設定工作目錄
WORKDIR /app

# 建立非 root 使用者以提升安全性
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 從建置階段複製編譯好的 JAR 檔案
COPY --from=builder /app/target/*.jar app.jar

# 暴露應用程式端口
# Spring Boot 預設使用 8080 port
EXPOSE 8080

# 健康檢查設定
# 每 30 秒檢查一次 /health 端點
# 容器啟動後 40 秒才開始檢查（給應用程式時間啟動）
# 連續失敗 3 次則標記為 unhealthy
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# 設定 JVM 參數和環境變數
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport"

# 啟動應用程式
# 使用 exec 形式確保 Java 程序是 PID 1，可以正確接收信號
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
