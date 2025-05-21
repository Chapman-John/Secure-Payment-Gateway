# Secure Payment Gateway Application

A comprehensive online banking solution built with Spring Boot and React, featuring robust security, real-time notifications, and fraud detection.

## Technologies Used

### Backend
- Java 17
- Spring Boot 3.3.5
- Spring Security with JWT Authentication
- Spring Data JPA (Hibernate)
- WebSockets with STOMP for real-time communication
- Flyway for database migrations
- H2 Database (development) / MySQL (production)
- Lombok for reduced boilerplate
- JUnit and Testcontainers for testing

### Frontend
- React 18.2.0 with TypeScript
- React Router 6.20.0 for navigation
- Tailwind CSS 3.4.17 for styling
- STOMP/SockJS for WebSocket client
- Axios 1.7.9 for API requests
- Heroicons for UI components

## Key Features

### Authentication & Security
- JWT-based authentication system
- Password encryption with BCrypt
- Account locking after multiple failed login attempts
- Session tracking with IP, device, and timestamp logging
- CORS configuration for secure cross-origin requests

### Account Management
- Multi-account support (Savings, Checking, Credit)
- Real-time balance inquiries
- Deposits and withdrawals
- Account-to-account transfers
- Contact management for frequent transfers

### Transaction System
- Comprehensive transaction history
- Multiple transaction types (Transfer, Deposit, Withdrawal)
- Transaction status tracking (Pending, Completed, Failed, Flagged)
- Reference number generation for tracking

### Advanced Security Features
- Real-time fraud detection for unusual transactions
- Automated suspicious transaction flagging
- Login attempt tracking and account locking
- IP and device tracking
- Multi-factor authentication support

### Real-time Notifications
- WebSocket-based instant notifications
- Multiple notification types (Transaction, Security, System)
- Severity levels (Info, Warning, Critical)
- Customizable notification preferences
- Email notification support
- SMS notification support

### Notification Preferences
- Granular control over notification channels
- Channel-specific thresholds for transaction notifications
- Per-category notification settings
- Real-time update of notification settings

## Architecture

The application follows a modern microservices-oriented architecture:

- **Controller Layer**: RESTful APIs for client communication
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access and persistence
- **Entity Layer**: Domain models with JPA annotations
- **Security Layer**: JWT-based authentication and authorization
- **Notification Layer**: Real-time and asynchronous notifications via WebSockets

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

### Notification Management
- `GET /api/notifications/{accountId}` - Get all notifications
- `GET /api/notifications/{accountId}/unread` - Get unread notifications
- `PUT /api/notifications/{notificationId}/read` - Mark notification as read
- `GET /api/notifications/preferences/{accountId}` - Get notification preferences
- `PUT /api/notifications/preferences/{accountId}` - Update notification preferences

### WebSocket Endpoints
- `/ws` - WebSocket connection endpoint with SockJS fallback
- `/topic/notifications/{userId}` - User-specific notification subscription

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

## Database Configuration

The application uses Flyway for database migrations, allowing for:
- Versioned database schema changes
- Environment-specific database configuration
- Automatic schema creation and updates
- Data seeding for development environments

Database files:
- `V1__create_base_tables.sql` - Initial schema creation
- `V2__add_notification_tables.sql` - Notification system tables
- `data.sql` - Sample data for development

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
- Account locking mechanism protects against brute force attacks

## Future Enhancements
- Mobile application integration
- International wire transfers
- Investment account support
- Enhanced analytics dashboard
- AI-powered spending insights
- Biometric authentication
- Scheduled recurring payments

## License
This project is available under the MIT License.
