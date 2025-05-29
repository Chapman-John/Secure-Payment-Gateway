-- BCrypt encoded version of 'password123'
-- The actual value is the bcrypt hash of 'password123'
INSERT INTO accounts (account_holder_name, username, password, account_number, balance, account_type) VALUES 
('John Doe', 'john', '$2a$10$rPizzuJ5VcGW.fnP75Nt5.qaJA.O8wjF8zxAyPeP9KzUzw3jWP5je', '1234567890', 1000.00, 'SAVINGS'),
('Jane Doe', 'jane', '$2a$10$rPizzuJ5VcGW.fnP75Nt5.qaJA.O8wjF8zxAyPeP9KzUzw3jWP5je', '0987654321', 2500.00, 'CHECKING');
