# JournalApp

A RESTful journal management backend built with **Java and Spring Boot**. JournalApp lets users create and manage personal journal entries through authenticated APIs. It also integrates Redis caching, weather data, Kafka-based sentiment notifications, and email delivery.

> **Note:** The repository's Maven configuration targets Java 8 and Spring Boot 2.7.16. The project description says “E2EE Journal App,” but this README describes the security features visible in the code (JWT authentication and BCrypt password hashing) and does not claim that end-to-end encryption is implemented.

## Features

- **User accounts** — sign up, log in, and manage user details.
- **JWT authentication** — stateless authentication for protected API routes.
- **Password hashing** — BCrypt is used to hash passwords before storage.
- **Role-based authorization** — user routes and admin-only routes have separate access rules.
- **Journal entry management** — create, view, update, and delete entries associated with a user.
- **MongoDB persistence** — stores users and journal entries.
- **Redis caching** — caches weather API responses with a time-to-live to reduce repeated requests.
- **Weather integration** — retrieves weather information through an external weather API.
- **Kafka integration** — consumes weekly sentiment messages and sends email notifications in the development profile.
- **Email support** — sends email through Spring's mail integration when configured.
- **Validation and tests** — includes Bean Validation and Spring Boot test dependencies.
- **Logging** — uses Logback for application logging.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 8 | Programming language |
| Spring Boot 2.7.16 | Application framework |
| Spring Web | REST APIs |
| Spring Security | Authentication and authorization |
| JWT (JJWT 0.12.5) | Token-based authentication |
| Spring Data MongoDB | Database access |
| MongoDB | Persistent storage |
| Spring Data Redis | Caching |
| Redis | Cache store |
| Spring for Apache Kafka | Message consumption |
| Spring Boot Starter Mail | Email delivery |
| Maven | Build and dependency management |
| Lombok | Boilerplate reduction |
| JUnit / Spring Boot Test | Testing |

## Project Structure

```text
src/
├── main/
│   ├── java/net/engineeringdigest/journalApp/
│   │   ├── Controller/     # REST controllers
│   │   ├── cache/          # Application cache
│   │   ├── config/         # Security and Redis configuration
│   │   ├── constants/      # Constants and placeholders
│   │   ├── entity/         # MongoDB entities
│   │   ├── enums/          # Enums such as sentiment
│   │   ├── filter/         # JWT request filter
│   │   ├── model/          # Kafka message models
│   │   ├── repository/     # Data access
│   │   ├── response/       # API response models
│   │   ├── schedular/      # Scheduled tasks
│   │   ├── service/        # Business logic and integrations
│   │   └── utils/          # JWT utilities
│   └── resources/          # Application configuration and logging
└── test/                   # Automated tests
```

## Getting Started

### Prerequisites

Install or arrange access to:

- JDK 8
- Git
- MongoDB (local or hosted)
- Redis (local or hosted)
- Kafka, if you want to run the sentiment-consumer workflow
- SMTP credentials, if you want to send email
- A weather API key, if you want to use the weather endpoint

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/<your-repository>.git
cd journalApp
```

Replace the URL with your repository URL.

### 2. Configure the application

The checked-in `application.yml` activates the `dev` profile by default. The development configuration file is not included in this repository archive, so create `src/main/resources/application-dev.yml` locally, or configure an equivalent profile with your own values.

Example development configuration:

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGO_URI}
      auto-index-creation: true
  redis:
    url: ${REDIS_URL}
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

server:
  port: ${PORT:8080}

weather:
  api:
    key: ${WEATHER_API_KEY}

app:
  personal:
    email: ${APP_EMAIL}

kafka:
  bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
```

Use your own credentials and service URLs. The exact configuration may need adjustment for your Kafka setup and local services. Do not commit passwords, API keys, JWT signing secrets, or database credentials.

For the production profile, the included `application-prod.yml` expects these environment variables:

| Variable | Purpose |
|---|---|
| `MONGO_URI` | MongoDB connection URI |
| `REDIS_URL` | Redis connection URL |
| `MAIL_USERNAME` | SMTP account username |
| `MAIL_PASSWORD` | SMTP/app password |
| `PORT` | Port provided by the deployment platform |
| `KAFKA_SERVERS` | Kafka bootstrap server address |
| `WEATHER_API_KEY` | Weather API key |
| `APP_EMAIL` | Application email address used by the project |

Activate the production profile by setting:

```bash
SPRING_PROFILES_ACTIVE=prod
```

On Windows PowerShell, for example:

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod"
```

Set environment variables through your hosting provider's dashboard for deployment. Never paste real secrets into the README or source control.

### 3. Run the application

Using the Maven wrapper:

**Windows**
```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS**
```bash
./mvnw spring-boot:run
```

Or build and run the JAR:

```bash
./mvnw clean package
java -jar target/journalApp-0.0.1-SNAPSHOT.jar
```

The default application port is commonly `8080`, unless overridden by configuration. The production configuration sets the servlet context path to `/journal`.

## API Overview

The controller mappings in the repository include the following routes. The effective URL can also depend on the active profile's servlet context path and security configuration.

| Method | Endpoint | Purpose | Access |
|---|---|---|---|
| `GET` | `/public/health-check` | Health check | Public |
| `POST` | `/public/signup` | Register a user | Public |
| `POST` | `/public/login` | Authenticate and obtain a JWT | Public |
| `GET` | `/journal` | List the authenticated user's journal entries | Authenticated |
| `POST` | `/journal` | Create a journal entry | Authenticated |
| `GET` | `/journal/id/{myId}` | Retrieve an entry by ID | Authenticated; ownership should be enforced |
| `PUT` | `/journal/id/{myId}` | Update an entry | Authenticated |
| `DELETE` | `/journal/id/{id}` | Delete an entry | Authenticated |
| `GET` | `/user` | Get the current user's details | Authenticated |
| `PUT` | `/user` | Update user details | Authenticated |
| `DELETE` | `/user` | Delete the current user | Authenticated |
| `GET` | `/admin/all-users` | List users | Admin |
| `POST` | `/admin/create-admin` | Create an admin user | Admin |
| `GET` | `/admin/clear-app-cache` | Clear application cache | Admin |

### Authentication flow

1. Register using `POST /public/signup`.
2. Log in using `POST /public/login`.
3. Use the returned JWT in the `Authorization` header for protected routes:

```http
Authorization: Bearer <your-jwt-token>
```

Use the request DTO/body fields expected by the corresponding controller. Check the controller source for exact JSON payloads and response formats.

## Running Tests

Run the test suite with:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

Some integration tests may require MongoDB, Redis, Kafka, or mail configuration. Configure the required services before running those tests.

## Deployment

The project includes a production Spring profile and can be deployed to a Java-compatible hosting service.

1. Push the project to GitHub.
2. Configure the required environment variables in your hosting provider.
3. Ensure the MongoDB, Redis, Kafka, and SMTP services are reachable from the deployment environment.
4. Set `SPRING_PROFILES_ACTIVE=prod`.
5. Configure the build command to use the Maven wrapper and the start command to run the generated JAR, if required by the platform.

The production configuration uses `server.port: ${PORT}` and the `/journal` context path. Confirm the health-check URL and platform port settings after deployment.

## Security Notes

- Passwords are BCrypt-hashed before being stored.
- Protected endpoints use JWT-based stateless authentication.
- Admin endpoints require the `ADMIN` role.
- Keep credentials and API keys in environment variables or a secret manager.
- Use HTTPS in production.
- Review authorization and ownership checks for every journal-entry operation before exposing the service publicly.
- JWT authentication and password hashing do **not** by themselves mean the journal contents are end-to-end encrypted.

## Future Improvements

- Add API documentation with OpenAPI/Swagger.
- Add integration tests using isolated test services or containers.
- Add request/response examples and centralized exception handling.
- Add pagination and filtering for journal entries.
- Add refresh-token and token-expiration handling where appropriate.
- Add CI checks for formatting, tests, and dependency/security scanning.

## Contributing

Contributions and suggestions are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Make your changes and add tests where appropriate.
4. Open a pull request describing the change.

## Author

**Aman Abbasi**

---

If you found this project useful, consider giving the repository a ⭐ on GitHub.
