package com.api.docmcp;

import com.api.docmcp.service.SwaggerUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class DocMcpApplication {

    public static void main(String[] args) {
        log.info("启动MCP服务器应用...");
        SpringApplication.run(DocMcpApplication.class, args);
        log.info("MCP服务器应用启动完成");
    }

    /**
     * 注册Swagger工具到MCP服务器
     */
    @Bean
    public ToolCallbackProvider swaggerTools(SwaggerUploadService swaggerUploadService) {
        log.info("=== 开始注册Swagger工具到MCP服务器 ===");
        log.info("SwaggerUploadService实例: {}", swaggerUploadService);

        MethodToolCallbackProvider provider = MethodToolCallbackProvider.builder()
                .toolObjects(swaggerUploadService)
                .build();

        log.info("ToolCallbackProvider创建成功: {}", provider);
        log.info("=== Swagger工具注册完成 ===");

        return provider;
    }
}
