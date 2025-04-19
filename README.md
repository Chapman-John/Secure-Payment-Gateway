# Online Banking Application

A comprehensive online banking solution built with Spring Boot and React, featuring robust security, real-time notifications, and fraud detection.

## Technologies Used
- **Backend**
  - Java 17
  - Spring Boot 3.3.5
  - Spring Security with JWT Authentication
  - Spring Data JPA (Hibernate)
  - WebSockets for real-time updates
  - H2 Database (development) / MySQL (production)
  - Lombok for reduced boilerplate
  - JUnit and Testcontainers for testing

- **Frontend**
  - React
  - Tailwind CSS
  - WebSocket client for real-time updates
  - Node.js

## Key Features

### Authentication & Security
- JWT-based authentication system
- Password encryption with BCrypt
- Two-factor authentication via SMS or email
- Session tracking and management
- CORS configuration for secure cross-origin requests

### Account Management
- Multi-account support (Savings, Checking)
- Balance inquiries
- Deposits and withdrawals
- Account-to-account transfers
- Contact management for frequent transfers

### Transaction System
- Comprehensive transaction history
- Multiple transaction types (Transfer, Deposit, Withdrawal)
- Transaction status tracking (Pending, Completed, Failed, Flagged)
- Reference number generation for tracking

### Advanced Security Features
- Fraud detection system for unusual transactions
- Login attempt tracking and account locking
- IP and device tracking
- Location-based security

### Real-time Notifications
- WebSocket-based instant notifications
- Email notifications for significant transactions
- SMS alerts for all account activities
- Two-factor authentication code delivery

### Recurring Payments
- Scheduled recurring transfers
- Multiple frequency options (Daily, Weekly, Monthly, Yearly)
- Active/inactive payment management

## Architecture

The application follows a modern microservices-oriented architecture:

- **Controller Layer**: RESTful APIs for client communication
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access and persistence
- **Entity Layer**: Domain models with JPA annotations
- **Security Layer**: Authentication, authorization, and data protection
- **Notification Layer**: Real-time and asynchronous notifications

## API Endpoints

### Authentication
- `POST /api/auth/**` - Authentication endpoints

### Account Management
- `POST /api/accounts/register` - Create new account
- `POST /api/accounts/login` - Login to account
- `GET /api/accounts` - List all accounts
- `GET /api/accounts/{id}` - Get account details
- `GET /api/accounts/{id}/balance` - Get account balance
- `POST /api/accounts/{id}/deposit` - Deposit funds
- `POST /api/accounts/{id}/withdraw` - Withdraw funds
- `POST /api/accounts/{fromId}/transfer/{toId}` - Transfer between accounts

### WebSocket Endpoints
- `/ws` - WebSocket connection endpoint
- `/topic/transactions/{id}` - Transaction notifications subscription

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Node.js 14+ and npm
- MySQL (optional for production)

### Backend Setup
1. Clone the repository
2. Navigate to the backend directory:
   ```bash
   cd online-banking-backend
   ```
3. Build the application:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   The backend will start at `http://localhost:8080`

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd online-banking-frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm start
   ```
   The frontend will be available at `http://localhost:5173`

## Testing
The application includes comprehensive test coverage:

```bash
mvn test
```

Integration tests use Testcontainers to spin up a MySQL database in a Docker container, ensuring tests run in an environment similar to production.

## Deployment

### Docker (Recommended)
Docker configurations are included for easy deployment:

```bash
docker-compose up
```

### Traditional Deployment
For manual deployment to a server:

1. Build the application:
   ```bash
   mvn clean package -Pprod
   ```
2. Deploy the resulting JAR file:
   ```bash
   java -jar target/online-banking-backend-0.0.1-SNAPSHOT.jar
   ```

## Security Considerations
- The JWT secret key should be stored in environment variables
- Database credentials should be externalized
- Use HTTPS in production
- Consider implementing rate limiting

## Future Enhancements
- Mobile application integration
- International wire transfers
- Investment account support
- Enhanced analytics dashboard
- AI-powered spending insights

## License
This project is available under both:
- [MIT License](https://opensource.org/licenses/MIT)
- [GNU General Public License (GPL)](https://www.gnu.org/licenses/gpl-3.0.html)

## Contributors
- [Your Name]
- [Other Contributors]
