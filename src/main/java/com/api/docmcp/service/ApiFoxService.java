package com.api.docmcp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * ApiFox API集成服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiFoxService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${apifox.api.base-url:https://api.apifox.com}")
    private String apiFoxBaseUrl;

    @Value("${apifox.api.version:2024-03-28}")
    private String apiVersion;

    @Value("${apifox.api.project-id:}")
    private String defaultProjectId;

    @Value("${apifox.api.access-token:}")
    private String defaultAccessToken;

    /**
     * 上传Swagger JSON到ApiFox
     *
     * @param projectId 项目ID
     * @param accessToken 访问令牌
     * @param swaggerJson Swagger JSON字符串
     * @param options 导入选项
     * @return 上传结果
     */
    public Mono<Map<String, Object>> uploadSwaggerToApiFox(String projectId, String accessToken, 
                                                          String swaggerJson, Map<String, Object> options) {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(apiFoxBaseUrl)
                    .build();

            // 构建请求体 - 直接传递JSON字符串，过滤null值
            Map<String, Object> requestBody = new java.util.HashMap<>();
            if (swaggerJson != null && !swaggerJson.trim().isEmpty()) {
                requestBody.put("input", swaggerJson);
            }

            // 如果有其他非null参数也可以在这里添加
            // if (options != null) {
            //     requestBody.put("options", options);
            // }

            // 格式化JSON用于Postman测试，排除null值
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
                String formattedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody);
                log.info("=== ApiFox请求体 (Postman格式) ===");
                log.info("\n{}", formattedJson);
                log.info("=== 请求URL ===");
                log.info("POST {}/v1/projects/{}/import-openapi?locale=zh-CN", apiFoxBaseUrl, projectId);
                log.info("=== 请求头 ===");
                log.info("Authorization: Bearer {}", accessToken);
                log.info("X-Apifox-Api-Version: {}", apiVersion);
                log.info("Content-Type: application/json");
                log.info("===============================");
            } catch (Exception e) {
                log.debug("ApiFox请求体: {}", requestBody);
            }

            log.info("开始上传到ApiFox - 项目ID: {}, API版本: {}", projectId, apiVersion);

            return webClient.post()
                    .uri("/v1/projects/{projectId}/import-openapi?locale=zh-CN", projectId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("X-Apifox-Api-Version", apiVersion)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> {
                                        log.error("ApiFox API错误响应 - 状态码: {}, 响应体: {}", response.statusCode(), body);
                                        return new RuntimeException(String.format("ApiFox API调用失败: %s - %s",
                                                response.statusCode(), body));
                                    }))
                    .bodyToMono(Map.class)
                    .map(result -> (Map<String, Object>) result)
                    .doOnSuccess(result -> log.info("成功上传到ApiFox: {}", result))
                    .doOnError(error -> log.error("上传到ApiFox失败", error));

        } catch (Exception e) {
            log.error("上传到ApiFox失败", e);
            return Mono.error(new RuntimeException("上传到ApiFox失败", e));
        }
    }
}
