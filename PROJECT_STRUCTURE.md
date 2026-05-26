# Smart Healthcare Project Structure

This document explains the structure of the Smart Healthcare project, which consists of an Android frontend and a Spring Boot backend with PostgreSQL integration.

## Project Overview

The Smart Healthcare application is designed to connect patients with doctors for telemedicine services. It includes features such as:

- User registration and authentication
- Doctor-patient communication
- Appointment scheduling
- Video calling
- Medical record management
- Prescription handling

## Directory Structure

```
SmartHealthcare/
├── app/                          # Android application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/smarthealthcare/
│   │   │   │   ├── activities/              # Activity classes
│   │   │   │   ├── adapters/                # RecyclerView adapters
│   │   │   │   ├── models/                  # Data models
│   │   │   │   ├── network/                 # Network utilities (Retrofit)
│   │   │   │   ├── SmartHealthcareApplication.java  # Custom Application class
│   │   │   │   └── MainActivity.java        # Main activity
│   │   │   ├── res/                         # Resources (layouts, drawables, etc.)
│   │   │   └── AndroidManifest.xml          # Application manifest
│   └── build.gradle                         # Android module build configuration
├── backend/                      # Spring Boot backend module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/smarthealthcare/backend/
│   │   │   │   ├── controller/              # REST controllers
│   │   │   │   ├── service/                 # Business logic
│   │   │   │   ├── repository/              # Data access layer
│   │   │   │   ├── model/                   # Entity classes
│   │   │   │   ├── dto/                     # Data transfer objects
│   │   │   │   ├── config/                  # Configuration classes
│   │   │   │   └── BackendApplication.java  # Main application class
│   │   │   └── resources/
│   │   │       └── application.properties   # Backend configuration
│   │   └── test/                            # Unit and integration tests
│   ├── build.gradle                         # Backend module build configuration
│   ├── README.md                            # Backend documentation
│   └── USAGE.md                             # Backend usage guide
├── build.gradle                             # Root project build configuration
├── settings.gradle                          # Project module settings
├── gradle.properties                        # Gradle properties
└── PROJECT_STRUCTURE.md                     # This file
```

## Modules

### 1. Android Application (app/)

The Android frontend is built using Java and follows the standard Android project structure.

**Key Components:**
- **Activities**: UI screens (Login, Register, Dashboard, etc.)
- **Adapters**: RecyclerView adapters for lists
- **Models**: Data models that match backend entities
- **Network**: Retrofit-based API client for backend communication

**Dependencies:**
- Firebase (for authentication and some data storage)
- Retrofit (for REST API communication)
- Jitsi Meet SDK (for video calling)
- MPAndroidChart (for data visualization)

### 2. Spring Boot Backend (backend/)

The backend is a RESTful API service built with Spring Boot that connects to PostgreSQL.

**Key Components:**
- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic
- **Repositories**: Handle data persistence
- **Models**: JPA entities that map to database tables
- **DTOs**: Data transfer objects for API communication

**Dependencies:**
- Spring Boot Web
- Spring Data JPA
- PostgreSQL Driver
- Spring Security (optional)

## Database Integration

### PostgreSQL Setup

The backend connects to a PostgreSQL database using the following configuration in `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smarthealthcare
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Database Schema

The application automatically creates tables using Hibernate. The main table is:

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

## Running the Application

### Backend

1. Ensure PostgreSQL is running
2. Create the database:
   ```sql
   CREATE DATABASE smarthealthcare;
   ```
3. Update database credentials in `application.properties`
4. Run the backend:
   ```bash
   cd backend
   ./gradlew bootRun
   ```

### Android App

1. Ensure the backend is running
2. Open the project in Android Studio
3. Build and run the application

## Testing

The backend includes unit and integration tests:

- **Repository Tests**: Test data access layer
- **Service Tests**: Test business logic
- **Controller Tests**: Test REST endpoints

Run tests with:
```bash
cd backend
./gradlew test
```

## Integration Points

### Android to Backend Communication

The Android app uses Retrofit to communicate with the backend:

1. **ApiClient**: Configures the Retrofit client
2. **ApiInterface**: Defines API endpoints
3. **Activities**: Use the API client to make network requests

Example usage in activities:
```java
ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
Call<User> call = apiInterface.createUser(user);
call.enqueue(new Callback<User>() {
    // Handle response
});
```

## Security Considerations

1. **Network Security**: The Android app is configured to allow HTTP connections to localhost for development
2. **CORS**: The backend is configured to allow cross-origin requests
3. **Data Validation**: Both frontend and backend should implement proper input validation
4. **Authentication**: In production, implement proper authentication mechanisms

## Future Enhancements

1. **Enhanced Security**: Implement JWT-based authentication
2. **Data Encryption**: Encrypt sensitive data in the database
3. **Caching**: Add Redis for caching frequently accessed data
4. **Monitoring**: Add logging and monitoring capabilities
5. **Documentation**: Generate API documentation using Swagger