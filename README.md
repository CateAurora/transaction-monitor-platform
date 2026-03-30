# Transaction Monitor Platform

交易监控平台，用于实时监控服务异常、分析根因并发送预警通知。

## 项目结构

- `src/main/java/com/trading/monitor/` - 主代码目录
  - `entity/` - 实体类
  - `mapper/` - MyBatis-Plus Mapper 接口
  - `service/` - 业务逻辑层
  - `consumer/` - Kafka 消费者
  - `analysis/` - 大模型分析相关
  - `alert/` - 预警通知
  - `config/` - 配置类
  - `controller/` - REST 接口
  - `client/` - 外部服务客户端
  - `event/` - 事件类
  - `listener/` - 事件监听器

- `src/main/resources/` - 资源目录
  - `application.yml` - 配置文件
  - `db/` - 数据库脚本

## 技术栈

- Spring Boot 3.2.4
- MyBatis-Plus 3.5.6
- Kafka
- OpenAI API
- Docker

## 快速启动

### 环境要求

- Docker
- Docker Compose

### 启动步骤

1. **构建项目**

   ```bash
   mvn clean package -DskipTests
   ```

2. **启动服务**

   ```bash
   docker-compose up -d
   ```

   这会启动以下服务：
   - MySQL 8.0（端口：3306）
   - Kafka（端口：9092）
   - 监控平台（端口：8080）

3. **查看日志**

   ```bash
   docker-compose logs -f
   ```

4. **测试接口**

   发送测试预警：
   ```bash
   curl -X POST http://localhost:8080/api/alert/test \
   -H "Content-Type: application/json" \
   -d '{"serviceName": "test-service", "riskLevel": "HIGH"}'
   ```

### 环境变量

需要设置以下环境变量：

- `OPENAI_API_KEY` - OpenAI API 密钥
- `DINGTALK_WEBHOOK` - 钉钉机器人 Webhook 地址
- `EMAIL_USERNAME` - 邮件发件人用户名
- `EMAIL_PASSWORD` - 邮件发件人密码
- `EMAIL_FROM` - 邮件发件人地址

### 停止服务

```bash
docker-compose down
```