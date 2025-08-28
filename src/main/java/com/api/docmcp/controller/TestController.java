package com.api.docmcp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 测试控制器 - 用于验证服务器状态
 */
@Slf4j
@RestController
public class TestController {

    @GetMapping("/test")
    public Map<String, Object> test() {
        log.info("=== 测试端点被调用 ===");
        return Map.of(
                "status", "ok",
                "message", "MCP服务器正在运行",
                "timestamp", LocalDateTime.now(),
                "server", "doc-mcp-server"
        );
    }

    @GetMapping("/mcp/test")
    public Map<String, Object> mcpTest() {
        log.info("=== MCP测试端点被调用 ===");
        return Map.of(
                "status", "ok",
                "message", "MCP端点可访问",
                "timestamp", LocalDateTime.now()
        );
    }
}
