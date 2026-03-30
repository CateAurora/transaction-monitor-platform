-- 创建数据库
CREATE DATABASE IF NOT EXISTS monitor_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE monitor_db;

-- 创建异常记录表
CREATE TABLE IF NOT EXISTS exception_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(255) NOT NULL,
    exception_type VARCHAR(50) NOT NULL,
    error_code VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    stack_trace TEXT,
    trace_id VARCHAR(100) NOT NULL,
    span_id VARCHAR(100) NOT NULL,
    timestamp DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建分析结果表
CREATE TABLE IF NOT EXISTS analysis_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exception_id BIGINT NOT NULL,
    model_used VARCHAR(255) NOT NULL,
    suggestion TEXT NOT NULL,
    root_cause TEXT NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exception_id) REFERENCES exception_record(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建预警历史表
CREATE TABLE IF NOT EXISTS alert_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exception_id BIGINT NOT NULL,
    alert_type VARCHAR(100) NOT NULL,
    receiver VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    sent_at DATETIME NOT NULL,
    FOREIGN KEY (exception_id) REFERENCES exception_record(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建交易表
CREATE TABLE IF NOT EXISTS transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_id VARCHAR(100) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建预测结果表
CREATE TABLE IF NOT EXISTS prediction_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(255) NOT NULL,
    predicted_exception_type VARCHAR(255) NOT NULL,
    prediction_reason TEXT NOT NULL,
    prevention_suggestion TEXT NOT NULL,
    prediction_time DATETIME NOT NULL,
    predicted_period_start DATETIME NOT NULL,
    predicted_period_end DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;