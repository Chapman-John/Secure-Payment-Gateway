-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    severity VARCHAR(20) NOT NULL,
    reference_id BIGINT,
    reference_type VARCHAR(50),
    additional_data TEXT,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Create notification preferences table
CREATE TABLE IF NOT EXISTS notification_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    enable_real_time_notifications BOOLEAN DEFAULT TRUE,
    enable_email_notifications BOOLEAN DEFAULT TRUE,
    email_for_transactions BOOLEAN DEFAULT TRUE,
    email_for_security BOOLEAN DEFAULT TRUE,
    email_for_system BOOLEAN DEFAULT FALSE,
    email_transaction_threshold DOUBLE DEFAULT 100.0,
    enable_sms_notifications BOOLEAN DEFAULT TRUE,
    sms_for_transactions BOOLEAN DEFAULT TRUE,
    sms_for_security BOOLEAN DEFAULT TRUE,
    sms_for_system BOOLEAN DEFAULT FALSE,
    sms_transaction_threshold DOUBLE DEFAULT 500.0,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);