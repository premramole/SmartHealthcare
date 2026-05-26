# Smart Healthcare Backend

This is the backend service for the Smart Healthcare Android application. It provides RESTful APIs for the mobile app to interact with a PostgreSQL database.

## Prerequisites

1. Java 17 or higher
2. PostgreSQL database
3. Gradle (included in the project)

## Database Setup

1. Install PostgreSQL on your system
2. Create a database named `smarthealthcare`:
   ```sql
   CREATE DATABASE smarthealthcare;
   ```
3. Create a user (optional, or use the default postgres user):
   ```sql
   CREATE USER smarthuser WITH PASSWORD 'smarthpass';
   GRANT ALL PRIVILEGES ON DATABASE smarthealthcare TO smarthuser;
   ```

## Configuration

Update the database configuration in `src/main/resources/application.properties`:

```properties
# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/smarthealthcare
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Running the Backend

### Using Gradle (Recommended)

```bash
# Navigate to the backend directory
cd backend

# Run the application
./gradlew bootRun
```

### Using Java

```bash
# Navigate to the backend directory
cd backend

# Build the application
./gradlew build

# Run the application
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### User Management

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/user/{userId}` - Get user by user ID
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update user by ID
- `DELETE /api/users/{id}` - Delete user by ID

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user

## Testing the API

You can test the API endpoints using tools like Postman or curl:

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "name": "John Doe",
    "email": "john@example.com",
    "role": "patient"
  }'

# Get all users
curl -X GET http://localhost:8080/api/users
```

## Project Structure

```
backend/
├── src/main/java/com/example/smarthealthcare/backend/
│   ├── BackendApplication.java          # Main application class
│   ├── controller/                      # REST controllers
│   ├── service/                         # Business logic
│   ├── repository/                      # Data access layer
│   ├── model/                           # Entity classes
│   └── dto/                             # Data transfer objects
└── src/main/resources/
    └── application.properties           # Configuration file
```

## Database Schema

The application automatically creates the necessary tables using Hibernate. The main table is:

### Users Table
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

## Troubleshooting

1. **Database Connection Issues**: Ensure PostgreSQL is running and the connection details in `application.properties` are correct.

2. **Port Conflicts**: If port 8080 is already in use, change `server.port` in `application.properties`.

3. **Missing Dependencies**: Run `./gradlew build` to ensure all dependencies are downloaded.

4. **Hibernate Issues**: Check the logs for any database schema creation errors.