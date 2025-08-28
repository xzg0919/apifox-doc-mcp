package com.api.docmcp.service;

import com.api.docmcp.service.ApiFoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Swagger上传到ApiFox的服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SwaggerUploadService {

    private final ApiFoxService apiFoxService;

    // 使用@PostConstruct在Bean初始化后添加日志
    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("=== SwaggerUploadService 初始化完成 ===");
        log.info("ApiFoxService: {}", apiFoxService);
    }

    @Tool(description = "获取Swagger参数规范示例，返回标准的Swagger JSON格式规范，基于Petstore示例。使用此工具了解正确的Swagger JSON结构，然后可以参考此格式创建自己的API文档并使用uploadSwaggerToApiFox工具上传。")
    public String getSwaggerSpecification() {
        try {
            log.info("=== 获取Swagger规范工具被调用 ===");
            
            // 从classpath读取petstore swagger.json
            ClassPathResource resource = new ClassPathResource("petstore-swagger.json");
            String swaggerJson = resource.getContentAsString(StandardCharsets.UTF_8);
            
            log.info("成功读取Swagger规范示例");
            
            return String.format("""
                    Swagger参数规范示例（基于Petstore）：
                    
                    这是一个完整的Swagger 2.0 JSON格式示例，包含以下主要部分：
                    
                    1. **基本信息 (info)**：
                       - title: API标题
                       - version: API版本
                       - description: API描述
                       - contact: 联系信息
                       - license: 许可证信息
                    
                    2. **服务器信息**：
                       - host: 主机地址
                       - basePath: 基础路径
                       - schemes: 支持的协议
                    
                    3. **标签 (tags)**：API分组标签
                    
                    4. **路径 (paths)**：具体的API端点定义
                    
                    5. **安全定义 (securityDefinitions)**：认证方式
                    
                    6. **数据模型 (definitions)**：请求/响应的数据结构
                    
                    完整的Swagger JSON：
                    %s
                    
                    使用说明：
                    - 参考此格式创建你自己的API文档
                    - 修改info部分的基本信息
                    - 定义你的API路径和方法
                    - 创建相应的数据模型
                    - 然后使用uploadSwaggerToApiFox工具上传到ApiFox

                    重要注意事项：
                    1. **准确的paths生成**：
                       - 仔细阅读代码和项目路由配置
                       - 根据实际的Controller路径和@RequestMapping注解生成准确的paths
                       - 确保paths与实际部署的API路径一致

                    2. **完整的接口参数和返回**：
                       - 仔细阅读接口代码，生成完整的参数定义
                       - 准确识别接口返回类型，如果有包装类应该使用包装类进行封装
                       - 包括所有必填和可选参数，以及正确的数据类型
                       - 确保响应模型与实际返回的数据结构完全一致
                       - 应避免无意义的参数 如权限参数或者token之类的公用接口参数

                    3. **随机模型名称**：
                       - 生成的参数和返回的数据模型应该使用随机的模型名称
                       - 避免因模型名称一致导致影响其他接口的数据模型
                       - 建议使用项目名+接口名+随机后缀的方式命名模型
                       - 例如：UserLoginRequest_abc123、ProductListResponse_xyz789
                    
                    4.**接口文件夹名称**：
                       - 如果能够获取到类型controller层的描述或者注释，可以作为接口文件夹的名称
                       - 如果没有，可以使用controller类名作为接口文件夹的名称
                    """, swaggerJson);
                    
        } catch (IOException e) {
            log.error("读取Swagger规范文件失败", e);
            return "获取Swagger规范失败: " + e.getMessage();
        } catch (Exception e) {
            log.error("执行获取Swagger规范工具失败", e);
            return "获取Swagger规范失败: " + e.getMessage();
        }
    }

    @Tool(description = "上传Swagger JSON到ApiFox平台。参数：projectId(ApiFox项目ID,必填), accessToken(ApiFox访问令牌,必填), swaggerJson(完整的Swagger JSON字符串,必填)。建议先使用getSwaggerSpecification工具获取规范格式。")
    public String uploadSwaggerToApiFox(
            String projectId,
            String accessToken,
            String swaggerJson) {
        
        try {
            log.info("===========================================");
            log.info("=== MCP工具 uploadSwaggerToApiFox 被调用 ===");
            log.info("===========================================");
            log.info("调用时间: {}", java.time.LocalDateTime.now());
            log.info("线程: {}", Thread.currentThread().getName());
            log.info("参数详情:");
            log.info("  - projectId: {}", projectId);
            log.info("  - accessToken: {}", accessToken != null ? "***已提供***" : "null");
            log.info("  - swaggerJson长度: {}", swaggerJson != null ? swaggerJson.length() : 0);
            log.info("开始上传Swagger JSON到ApiFox");

            // 验证参数
            if (projectId == null || projectId.trim().isEmpty()) {
                return "错误: projectId不能为空";
            }
            if (accessToken == null || accessToken.trim().isEmpty()) {
                return "错误: accessToken不能为空";
            }
            if (swaggerJson == null || swaggerJson.trim().isEmpty()) {
                return "错误: swaggerJson不能为空";
            }

            // 上传到ApiFox - 使用超时避免无限等待
            Map<String, Object> result = apiFoxService.uploadSwaggerToApiFox(projectId, accessToken, swaggerJson, null)
                    .timeout(java.time.Duration.ofSeconds(30)) // 30秒超时
                    .block(); // 同步等待结果

            return String.format("成功上传Swagger文档到ApiFox！\n项目ID: %s\n结果: %s", 
                    projectId, result);

        } catch (Exception e) {
            log.error("执行Swagger上传工具失败", e);
            return String.format("上传失败: %s", e.getMessage());
        }
    }
}
