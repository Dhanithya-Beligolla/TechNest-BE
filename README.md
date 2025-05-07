# Technest-BE

Technest-BE is the backend component of the Technest web application, a platform designed to foster knowledge sharing among technology enthusiasts. Built with **Spring Boot**, it provides RESTful APIs to support user management, tutorial and quiz management, post management, community interactions, and advertisement management. The backend integrates with a **PostgreSQL** database and **Redis** for data persistence and caching, respectively, both managed via Docker. **Cloudinary** is used for image storage, with image URLs stored in the database.

## Features

### 1. User Management
- **Admin Functionality**:
  - Register and log in with admin credentials.
  - View, monitor, and search user and admin activities.
  - Update or delete user accounts.
- **User Functionality**:
  - Register and log in using email/password or Google OAuth.
  - Update or delete personal profiles.

### 2. Tutorial Quiz Management
- **Admin Functionality**:
  - Create, edit, and delete tutorials with embedded multiple-choice quizzes.
- **User Functionality**:
  - Enroll in tutorials and complete quizzes.
  - View quiz scores and track progress via a dashboard.

### 3. Post Management
- **User Functionality**:
  - Create, edit, and delete posts with text and Cloudinary-hosted images.
  - Like and comment on posts.
  - Search posts using keywords.

### 4. Community Section
- **User Functionality**:
  - Post text-based questions.
  - Answer questions via comments.
  - Update or delete own questions and answers.

### 5. Advertisement Management
- **User Functionality**:
  - Create, edit, and delete advertisements with text and Cloudinary-hosted images.
  - Advertisements displayed based on visibility rules.

## Prerequisites
- **Java 17** or higher
- **Maven** for dependency management
- **Docker** and **Docker Compose** for PostgreSQL, Redis, and pgAdmin
- **Cloudinary** account for image storage
- **Google OAuth** credentials for authentication
- **Gmail SMTP** credentials for email functionality

## Project Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Dhanithya-Beligolla/TechNest-BE.git
cd technest-be
```

### 2. Configure Environment Variables
Update the `application.properties` file in `src/main/resources` with the following configuration, ensuring sensitive information (e.g., Cloudinary credentials, Google OAuth keys, Gmail SMTP password) is securely managed:

```properties
spring.application.name=TechNest-BE
server.reactive.session.cookie.same-site=strict
server.port=8080
spring.main.allow-circular-references=true

# Logging
logging.level.org.springframework.mail=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.com.example.technestbe=DEBUG

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/TechNestDB
spring.datasource.username=test
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Redis
spring.data.redis.host=localhost
spring.data.redis.password=
spring.data.redis.port=6379

# JWT
jwt.secret=<your-64-character-jwt-secret>
jwt.expiration=86400000

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<your-app-gmail>
spring.mail.password=<your-gmail-app-password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# OTP
otp.expiration=300000

# OAuth2
spring.security.oauth2.client.registration.google.client-id=<your-google-client-id>
spring.security.oauth2.client.registration.google.client-secret=<your-google-client-secret>
spring.security.oauth2.client.registration.google.scope=openid,email,profile
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google

# CORS
spring.web.cors.allowed-origins=http://localhost:5173
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE
spring.web.cors.allowed-headers=*
```

**Security Note**: Store sensitive values (e.g., `jwt.secret`, `spring.mail.password`, OAuth credentials) in a secure vault or environment variables, and avoid committing them to version control.

### 3. Set Up PostgreSQL, Redis, and pgAdmin with Docker
1. Ensure Docker is running.
2. Create a `docker-compose.yml` file in the project root with the following configuration:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:latest
    container_name: TechNest_postgres_container
    environment:
      POSTGRES_USER: test
      POSTGRES_PASSWORD: 1234
      POSTGRES_DB: TechNestDB
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:latest
    container_name: TechNest_redis_container
    ports:
      - "6379:6379"
    environment:
      REDIS_PASSWORD: test
    volumes:
      - redis_data:/data

  pgadmin:
    image: dpage/pgadmin4
    container_name: techNest_pgadmin_container
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@admin.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    depends_on:
      - postgres

volumes:
  postgres_data:
  redis_data:
```

3. Start the containers:
```bash
docker-compose up -d
```

4. Access pgAdmin at `http://localhost:5050` using the credentials:
   - Email: `admin@admin.com`
   - Password: `admin`

### 4. Build and Run the Spring Boot Application
1. Install dependencies and build the project:
```bash
mvn clean install
```
2. Run the application:
```bash
mvn spring-boot:run
```
3. The backend will be accessible at `http://localhost:8080`.

### 5. Testing
- Run unit and integration tests:
```bash
mvn test
```
- Ensure test coverage for critical components like authentication, database operations, and Redis caching.

## Troubleshooting
- **Database Connection Issues**: Verify PostgreSQL is running (`docker ps`) and `application.properties` has correct credentials (`spring.datasource.*`).
- **Redis Connection Issues**: Confirm Redis is running and `spring.data.redis.*` settings are correct.
- **Cloudinary Errors**: Ensure Cloudinary API keys are properly configured.
- **OAuth Issues**: Validate Google OAuth client ID and secret in `application.properties`.
- **Email Issues**: Check Gmail SMTP credentials and ensure the app password is used.

## Contribution
Contributions are welcome! Please submit issues or pull requests to enhance functionality or fix bugs.

## License
This project is licensed under the MIT License.
