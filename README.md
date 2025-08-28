# Doc-MCP: Swagger to ApiFox MCP Server

这是一个基于Spring Boot和Spring AI MCP的服务，提供Swagger规范获取和上传到ApiFox的功能。

## 项目特性

- 🚀 基于JDK 21和Spring Boot 3.5.5
- 🔧 使用Spring AI MCP 1.0.1实现MCP协议
- 📝 提供标准Swagger格式规范示例
- 🌐 集成ApiFox开放API，支持自动上传接口文档
- ⚡ 支持HTTP传输协议，可部署在云服务器上
- 🔗 通过HTTP SSE端点与AI助手无缝集成
- 📋 基于Petstore示例的完整Swagger规范

## 功能说明

MCP服务提供两个工具函数：

### 1. `getSwaggerSpecification`
获取Swagger参数规范示例，返回标准的Swagger JSON格式规范。

**功能**：
- 提供完整的Petstore Swagger JSON示例
- 详细的字段说明和使用指导
- 包含最佳实践建议

### 2. `uploadSwaggerToApiFox`
上传Swagger JSON到ApiFox平台。

**参数**：
- `projectId`: ApiFox项目ID (必填)
- `accessToken`: ApiFox访问令牌 (必填)
- `swaggerJson`: 完整的Swagger JSON字符串 (必填)

## 快速开始

### 1. 环境要求

- JDK 21
- Maven 3.6+

### 2. 编译和运行

```bash
# 编译项目
./mvnw clean compile

# 运行MCP服务器
./mvnw spring-boot:run
```

### 3. 云服务器部署

```bash
# 上传JAR包到云服务器
scp target/doc-mcp-0.0.1-SNAPSHOT.jar user@your-server.com:/opt/doc-mcp/

# 在云服务器上启动服务
ssh user@your-server.com
cd /opt/doc-mcp
nohup java -jar doc-mcp-0.0.1-SNAPSHOT.jar > mcp-server.log 2>&1 &
```

### 4. MCP客户端配置

在支持MCP的AI助手中添加以下配置：

```json
{
  "mcpServers": {
    "doc-mcp-server": {
      "url": "http://your-server.com:8080/sse"
    }
  }
}
```

本地开发配置：
```json
{
  "mcpServers": {
    "doc-mcp-server-local": {
      "url": "http://127.0.0.1:8080/sse"
    }
  }
}
```


## 使用示例

通过AI助手调用工具：

```
请帮我上传一个API文档到ApiFox：
- 项目ID: 4478210
- 访问令牌: your-access-token
```

## 配置说明

主要配置项在 `application.properties` 中：

```properties
# MCP服务器配置
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.stdio=true
spring.ai.mcp.server.name=doc-mcp-server
spring.ai.mcp.server.version=1.0.0
spring.ai.mcp.server.type=SYNC

# ApiFox API配置
apifox.api.base-url=https://api.apifox.com
apifox.api.version=2024-03-28
```

## 技术架构

- **Spring Boot**: 应用框架
- **Spring AI MCP**: MCP协议实现
- **WebFlux**: 响应式HTTP客户端
- **Jackson**: JSON处理
- **Swagger Parser**: OpenAPI规范处理

## 开发说明

项目结构：
```
src/main/java/com/api/docmcp/
├── DocMcpApplication.java          # 主启动类
├── service/
│   ├── SwaggerGeneratorService.java # Swagger JSON生成服务
│   ├── ApiFoxService.java          # ApiFox API集成服务
│   └── SwaggerUploadService.java   # MCP工具实现
└── config/
    └── WebClientConfig.java        # HTTP客户端配置
```
