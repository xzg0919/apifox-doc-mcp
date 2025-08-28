package com.api.docmcp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SwaggerGeneratorServiceTest {

    @Autowired
    private SwaggerGeneratorService swaggerGeneratorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGenerateSwaggerJson() throws Exception {
        // 准备测试数据
        Map<String, Object> apiInfo = Map.of(
                "title", "Test API",
                "version", "1.0.0",
                "description", "Test API Description",
                "servers", List.of(Map.of(
                        "url", "https://api.test.com",
                        "description", "Test Server"
                ))
        );

        // 执行测试
        String swaggerJson = swaggerGeneratorService.generateSwaggerJson(apiInfo);

        // 验证结果
        assertNotNull(swaggerJson);
        assertFalse(swaggerJson.isEmpty());

        // 验证JSON格式正确
        Map<String, Object> parsedJson = objectMapper.readValue(swaggerJson, Map.class);
        assertNotNull(parsedJson);

        // 验证基本结构
        assertTrue(parsedJson.containsKey("openapi") || parsedJson.containsKey("swagger"));
        assertTrue(parsedJson.containsKey("info"));

        @SuppressWarnings("unchecked")
        Map<String, Object> info = (Map<String, Object>) parsedJson.get("info");
        assertEquals("Test API", info.get("title"));
        assertEquals("1.0.0", info.get("version"));
        assertEquals("Test API Description", info.get("description"));

        System.out.println("Generated Swagger JSON:");
        System.out.println(swaggerJson);
    }

    @Test
    void testGenerateSwaggerJsonWithMinimalInfo() throws Exception {
        // 准备最小测试数据
        Map<String, Object> apiInfo = Map.of(
                "title", "Minimal API"
        );

        // 执行测试
        String swaggerJson = swaggerGeneratorService.generateSwaggerJson(apiInfo);

        // 验证结果
        assertNotNull(swaggerJson);
        assertFalse(swaggerJson.isEmpty());

        // 验证JSON格式正确
        Map<String, Object> parsedJson = objectMapper.readValue(swaggerJson, Map.class);
        assertNotNull(parsedJson);

        @SuppressWarnings("unchecked")
        Map<String, Object> info = (Map<String, Object>) parsedJson.get("info");
        assertEquals("Minimal API", info.get("title"));
        assertEquals("1.0.0", info.get("version")); // 默认值
        assertEquals("Generated API Documentation", info.get("description")); // 默认值
    }
}
