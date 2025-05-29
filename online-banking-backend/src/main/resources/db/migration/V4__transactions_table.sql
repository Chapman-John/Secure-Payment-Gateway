-- Create transactions table if it doesn't exist
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT,
    recipient_id BIGINT,
    amount DECIMAL(19,2) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    description TEXT,
    transaction_type VARCHAR(50) NOT NULL,
    reference_number VARCHAR(100),
    ip_address VARCHAR(50),
    device_info TEXT,
    location VARCHAR(255),
    is_fraud_suspected BOOLEAN DEFAULT FALSE,
    fraud_reason TEXT,
    FOREIGN KEY (sender_id) REFERENCES accounts(id),
    FOREIGN KEY (recipient_id) REFERENCES accounts(id)
);

-- Add new columns for enhanced transaction management (one at a time for H2 compatibility)
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS category VARCHAR(50);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS subcategory VARCHAR(100);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS is_recurring BOOLEAN DEFAULT FALSE;
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS recurring_pattern VARCHAR(50);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS merchant_name VARCHAR(255);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS merchant_category VARCHAR(100);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS merchant_location VARCHAR(255);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS is_disputed BOOLEAN DEFAULT FALSE;
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS dispute_reason TEXT;
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS dispute_date TIMESTAMP;
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS dispute_status VARCHAR(50);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS dispute_resolution TEXT;
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS notes TEXT;
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS tags VARCHAR(500);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS balance_after DECIMAL(19,2);

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_transactions_timestamp ON transactions(timestamp);
CREATE INDEX IF NOT EXISTS idx_transactions_sender_timestamp ON transactions(sender_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_transactions_category ON transactions(category);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions(status);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(transaction_type);
CREATE INDEX IF NOT EXISTS idx_transactions_disputed ON transactions(is_disputed);
CREATE INDEX IF NOT EXISTS idx_transactions_recurring ON transactions(is_recurring);

-- Sample data with timestamps
INSERT INTO transactions (sender_id, recipient_id, amount, timestamp, status, description, transaction_type, reference_number, category, merchant_name) VALUES 
(1, 2, 50.00, '2025-05-27 12:00:00', 'COMPLETED', 'Grocery shopping at Walmart', 'TRANSFER', 'TXN001', 'GROCERIES', 'Walmart'),
(1, NULL, 25.00, '2025-05-26 12:00:00', 'COMPLETED', 'Gas at Shell Station', 'WITHDRAWAL', 'TXN002', 'GAS', 'Shell'),
(2, 1, 100.00, '2025-05-25 12:00:00', 'COMPLETED', 'Dinner at McDonalds', 'TRANSFER', 'TXN003', 'DINING', 'McDonalds'),
(1, NULL, 1200.00, '2025-05-23 12:00:00', 'COMPLETED', 'Monthly rent payment', 'WITHDRAWAL', 'TXN004', 'BILLS', 'Property Management'),
(2, NULL, 75.50, '2025-05-21 12:00:00', 'COMPLETED', 'Amazon purchase', 'WITHDRAWAL', 'TXN005', 'SHOPPING', 'Amazon');