# Secure Payment Gateway Application

### Technologies Used
- **Java**  
- **Spring Boot Framework**  
- **H2 Database**  
- **JDBC**  
- **React**
- **Tailwind CSS**
- **Node.js**

This online banking application is built with Java Spring Boot, utilizing Spring Data JPA (Hibernate) and H2 in-memory database for data storage. The frontend is built with React and styled using Tailwind CSS.

--- 

## Features

### User Authentication System
- Account creation functionality  
- Login system with username/account number and password  

### Account Management
- View account balance  
- Deposit functionality  
- Withdrawal functionality  
- Transfer money between accounts  

### Transaction History
- View previous transactions  
- Get transaction details (date, amount, type)  

### Security Features
- Encryption for sensitive data  
- Input validation to prevent SQL injection and other attacks  
- Session management  

### Database Integration
- Store user information, account details, and transactions  
- Use JDBC or an ORM framework for database operations  

### Exception Handling
- Proper error handling for insufficient funds, invalid inputs, etc.  

### User Interface
- Console-based interface for basic applications  
- GUI using JavaFX or Swing for more advanced applications  

### Account Types
- Support for different account types (e.g., savings, checking)  

### Additional Features
- Interest calculation for savings accounts  
- Account search functionality  
- Account removal option  

### Code Structure
- Use object-oriented programming principles  
- Implement interfaces and classes for different bank operations  
- Organize code into packages for better management  

---

## Starting Up
Instructions for starting up the application will be provided here.  

To start the application, follow these steps:  

1. **Start Frontend Service with:**  
   ```bash
   npm install
   npm start
   ```

2. **Start Backend Service with:**
   ```bash
    mvn clean install
    mvn spring-boot::run

---

## License
This project is primarily licensed under the [MIT License](https://opensource.org/licenses/MIT).  

Additionally, you may choose to use this project under the terms of the [GNU General Public License (GPL)](https://www.gnu.org/licenses/gpl-3.0.html). 
