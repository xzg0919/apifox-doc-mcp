package com.api.docmcp.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Swagger JSON生成服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SwaggerGeneratorService {

    private final ObjectMapper objectMapper;

    /**
     * 根据自定义规则生成Swagger JSON
     *
     * @param apiInfo API信息
     * @return Swagger JSON字符串
     */
    public String generateSwaggerJson(Map<String, Object> apiInfo) {
        try {
            OpenAPI openAPI = new OpenAPI();
            
            // 设置基本信息
            Info info = new Info()
                    .title((String) apiInfo.getOrDefault("title", "API Documentation"))
                    .version((String) apiInfo.getOrDefault("version", "1.0.0"))
                    .description((String) apiInfo.getOrDefault("description", "Generated API Documentation"));
            openAPI.setInfo(info);
            
            // 设置服务器信息
            if (apiInfo.containsKey("servers")) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> servers = (List<Map<String, String>>) apiInfo.get("servers");
                for (Map<String, String> serverInfo : servers) {
                    Server server = new Server()
                            .url(serverInfo.get("url"))
                            .description(serverInfo.getOrDefault("description", ""));
                    openAPI.addServersItem(server);
                }
            }
            
            // 不添加示例数据，只保留基本信息
            
            // 配置ObjectMapper排除null值
            ObjectMapper cleanMapper = objectMapper.copy();
            cleanMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            
            return cleanMapper.writeValueAsString(openAPI);
        } catch (Exception e) {
            log.error("生成Swagger JSON失败", e);
            throw new RuntimeException("生成Swagger JSON失败", e);
        }
    }
}
