# transaction-monitor-platform
Based on Spring Boot + Kafka asynchronous consumption of microservice exception messages, integrate a large model (OpenAI API) for root cause analysis and suggestion generation, exploring practical integration scenarios between Java backend and large model capabilities.
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
