# Using the PostgreSQL Backend

This document explains how to set up and use the PostgreSQL backend for the Smart Healthcare application.

## Prerequisites

1. Java 17 or higher
2. PostgreSQL database server
3. Android Studio (for testing the Android app integration)

## Setting up PostgreSQL

### 1. Install PostgreSQL

Download and install PostgreSQL from https://www.postgresql.org/download/

### 2. Create Database and User

Connect to PostgreSQL as a superuser and run:

```sql
-- Create database
CREATE DATABASE smarthealthcare;

-- Create user (optional)
CREATE USER smarthuser WITH PASSWORD 'smarthpass';
GRANT ALL PRIVILEGES ON DATABASE smarthealthcare TO smarthuser;
```

### 3. Update Configuration

Edit `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smarthealthcare
spring.datasource.username=postgres
spring.datasource.password=your_password_here
```

## Running the Backend

### Option 1: Using Gradle Wrapper (Recommended)

```bash
# Navigate to the backend directory
cd backend

# Run the application
./gradlew bootRun
```

### Option 2: Build and Run JAR

```bash
# Navigate to the backend directory
cd backend

# Build the application
./gradlew build

# Run the JAR file
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

## Testing the Backend

Once the backend is running, you can test it using the following endpoints:

### User Management

1. **Get all users**
   ```
   GET http://localhost:8080/api/users
   ```

2. **Create a new user**
   ```
   POST http://localhost:8080/api/users
   Content-Type: application/json
   
   {
     "userId": "user123",
     "name": "John Doe",
     "email": "john@example.com",
     "role": "patient"
   }
   ```

3. **Get user by ID**
   ```
   GET http://localhost:8080/api/users/1
   ```

### Authentication

1. **Register a new user**
   ```
   POST http://localhost:8080/api/auth/register
   Content-Type: application/json
   
   {
     "userId": "user123",
     "name": "John Doe",
     "email": "john@example.com",
     "role": "patient"
   }
   ```

2. **Login**
   ```
   POST http://localhost:8080/api/auth/login
   Content-Type: application/json
   
   {
     "email": "john@example.com",
     "password": "password123"
   }
   ```

## Integrating with the Android App

The Android app uses Retrofit to communicate with the backend. Here's how to test the integration:

### 1. Start the Backend Server

Make sure the backend server is running on `http://localhost:8080`

### 2. Run the Android App

In Android Studio:
1. Select the BackendTestActivity as the launch activity
2. Run the app in an emulator or connected device

### 3. Test the Connection

In the app:
1. Press "Test Connection" to verify the backend is reachable
2. Press "Create User" to create a new user via the backend

## Troubleshooting

### Common Issues

1. **Connection Refused**: Make sure the backend server is running

2. **CORS Errors**: The backend is configured to allow CORS from localhost:3000 and localhost:8081

3. **Database Connection Failed**: 
   - Verify PostgreSQL is running
   - Check database credentials in `application.properties`
   - Ensure the database `smarthealthcare` exists

4. **Android Network Security**: 
   - The app is configured to allow HTTP connections to 10.0.2.2 (emulator host)
   - For physical devices, update the BASE_URL in ApiClient.java

### Logs

Check the console output for detailed error messages. The backend logs database operations and HTTP requests.

## API Documentation

### User Endpoints

| Method | Endpoint        | Description          |
|--------|-----------------|----------------------|
| GET    | /api/users      | Get all users        |
| GET    | /api/users/{id} | Get user by ID       |
| POST   | /api/users      | Create new user      |
| PUT    | /api/users/{id} | Update user by ID    |
| DELETE | /api/users/{id} | Delete user by ID    |

### Auth Endpoints

| Method | Endpoint            | Description     |
|--------|---------------------|-----------------|
| POST   | /api/auth/register  | Register user   |
| POST   | /api/auth/login     | Login user      |

## Database Schema

The application automatically creates the following table:

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL,
    medical_history TEXT,
    specialization VARCHAR(255),
    rating DOUBLE PRECISION,
    location VARCHAR(255),
    fees INTEGER
);
```

## Security Considerations

1. In production, use HTTPS instead of HTTP
2. Implement proper authentication and authorization
3. Use environment variables for sensitive configuration
4. Add input validation and sanitization
5. Implement rate limiting to prevent abuse