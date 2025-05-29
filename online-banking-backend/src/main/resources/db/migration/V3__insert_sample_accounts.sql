-- Insert sample accounts for testing
-- BCrypt encoded version of 'password123'
INSERT INTO accounts (id, account_holder_name, username, password, account_number, balance, account_type) VALUES 
(1, 'John Doe', 'john', '$2a$10$rPizzuJ5VcGW.fnP75Nt5.qaJA.O8wjF8zxAyPeP9KzUzw3jWP5je', '1234567890', 1000.00, 'SAVINGS'),
(2, 'Jane Doe', 'jane', '$2a$10$rPizzuJ5VcGW.fnP75Nt5.qaJA.O8wjF8zxAyPeP9KzUzw3jWP5je', '0987654321', 2500.00, 'CHECKING');