package dev.backend.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 應用程式主類別
 * 作為整個應用程式的入口點
 */
@SpringBootApplication // Spring Boot 主註解：啟用自動配置、元件掃描等功能
public class DemoApplication {

	/**
	 * 應用程式入口方法
	 * 啟動 Spring Boot 應用程式
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
