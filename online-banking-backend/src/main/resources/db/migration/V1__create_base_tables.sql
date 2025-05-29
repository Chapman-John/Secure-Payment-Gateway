CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_holder_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(19,2) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    last_login_ip VARCHAR(50),
    last_login_time TIMESTAMP,
    last_login_device VARCHAR(255),
    failed_login_attempts INT DEFAULT 0,
    is_locked BOOLEAN DEFAULT FALSE,
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    two_factor_secret VARCHAR(100),
    secret_expiry TIMESTAMP
);