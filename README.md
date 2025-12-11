# Security Spacee - Enterprise Application Template 🔐

Java 25 & Spring Boot 4 enterprise-grade application following **Hexagonal Architecture + Vertical Slicing** patterns.

> **Status:** Deploying with Dokploy GitHub Actions integration ✅

## 🎯 Quick Start

### Prerequisites

- Java 25+
- Gradle 8.x
- Docker & Docker Compose
- PostgreSQL 17+
- Redis 7+

### Local Development Setup

1. **Clone and Setup**
   ```bash
   git clone <repository>
   cd security-spacee
   ```

2. **Start Infrastructure**
   ```bash
   docker-compose up -d
   ```

3. **Run Application**
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

4. **API Documentation**
    - Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
    - OpenAPI JSON: http://localhost:8080/api/v1/api-docs

5. **Infrastructure Access**
    - PostgreSQL: localhost:5432 (user: spacecodee / password: SecurePass123!)
    - Redis: localhost:6379
    - Mailpit (Email Testing): http://localhost:8025

## 📁 Project Structure

```
security-spacee/
├── src/main/
│   ├── java/com/spacecodee/securityspacee/
│   │   ├── auth/                      # Authentication context
│   │   ├── user/                      # User management context
│   │   ├── passwordreset/             # Password reset context
│   │   ├── jwttoken/                  # JWT token management
│   │   └── common/                    # Shared utilities & configs
│   └── resources/
│       ├── application.yaml           # Base configuration
│       ├── application-local.yaml     # Local development
│       ├── application-test.yaml      # Testing with H2
│       ├── i18n/                      # Internationalization (ES, EN)
│       └── db/migration/              # Flyway migrations
├── src/test/java/                     # Unit & Integration Tests
├── docker-compose.yaml                # Infrastructure services
├── Dockerfile                         # Production-ready multi-stage build
└── build.gradle                       # Gradle configuration
```

## 🏗️ Architecture Principles

### Four-Layer Hexagonal Architecture

1. **Adapter Layer** - REST controllers, driven adapters
2. **Application Layer** - Use cases, port interfaces, DTOs
3. **Domain Layer** - Pure business logic, no frameworks
4. **Infrastructure Layer** - Database, external APIs, frameworks

### Vertical Slicing by Bounded Context

Each feature is completely self-contained within its bounded context folder, following the hexagonal pattern.

## 🔐 Key Features

- **JWT Authentication & Authorization** - Stateless security with database validation
- **Database-Driven Permissions** - Flexible role & permission model
- **Full i18n Support** - English (EN) and Spanish (ES)
- **User Registration & Password Reset** - Secure token-based workflows
- **Redis Caching** - Decorator pattern for transparent caching
- **Optimized Docker Builds** - Multi-stage, layered JAR, distroless base
- **Flyway Database Migrations** - Version-controlled schema management
- **OpenAPI/Swagger Documentation** - Auto-generated API docs
- **Health Checks & Monitoring** - Spring Boot Actuator integration

## 🧪 Testing

### Test Strategy (Pyramid)

- **Domain Tests** - Unit tests only, no Spring Context
- **Application Tests** - Unit tests with mocked ports
- **Infrastructure Tests** - Integration tests with H2 in-memory DB
- **Architecture Tests** - ArchUnit for dependency validation

### Run Tests

```bash
./gradlew test                          # All tests
./gradlew test --tests "*Domain*"       # Domain layer tests
./gradlew test --tests "*Integration*"  # Integration tests
```

## 🐳 Docker & Deployment

### Development (Compose)

```bash
docker-compose up -d                    # Start all services
docker-compose down -v                  # Stop and remove volumes
```

### Production (Single Container)

```bash
./gradlew bootJar -Djarmode=layertools  # Build layered JAR
docker build -t security-spacee:1.0 .   # Build image
docker run -p 8080:8080 security-spacee:1.0  # Run container
```

## ⚙️ Environment Configuration

### Environment Variables

All configuration is externalized via environment variables. Copy the example file and customize:

```bash
cp .env.example .env
```

Key environment variables:

| Variable                            | Description              | Example               |
| ----------------------------------- | ------------------------ | --------------------- |
| `DB_HOST`                           | PostgreSQL host          | `localhost`           |
| `DB_PORT`                           | PostgreSQL port          | `5432`                |
| `DB_NAME`                           | Database name            | `security_spacee_db`  |
| `DB_USERNAME`                       | Database user            | `spacecodee`          |
| `DB_PASSWORD`                       | Database password        | `SecurePass123!`      |
| `REDIS_HOST`                        | Redis host               | `localhost`           |
| `REDIS_PORT`                        | Redis port               | `6379`                |
| `JWT_SECRET_KEY`                    | JWT signing key (Base64) | `your-256-bit-secret` |
| `JWT_ACCESS_TOKEN_VALIDITY_MINUTES` | Access token expiry      | `30`                  |
| `JWT_REFRESH_TOKEN_VALIDITY_DAYS`   | Refresh token expiry     | `7`                   |

See `.env.example` for the complete list of available variables.

## ☕ Java 25 Configuration

### Native Access for Netty (Required)

Java 25 restricts native library access by default. This project uses Netty for Redis connections, which requires
explicit permission.

#### Running from Gradle

The `bootRun` and `test` tasks are pre-configured with the required JVM argument:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

#### Running from IntelliJ IDEA

A pre-configured run configuration is included at `.idea/runConfigurations/SecuritySpaceeApplication.xml`. If you need
to create a new one:

1. Go to `Run` → `Edit Configurations...`
2. Select your Spring Boot configuration
3. In **VM options**, add: `--enable-native-access=ALL-UNNAMED`
4. Click **Apply** and **OK**

### macOS Netty DNS Resolver (Platform-Specific)

The project includes a native Netty DNS resolver for improved performance on macOS. **This dependency is
platform-specific and must be adjusted based on your system:**

In `build.gradle`, locate the Netty dependency:

```gradle
// Netty DNS resolver for macOS Intel (match Spring Boot's Netty version)
runtimeOnly 'io.netty:netty-resolver-dns-native-macos:4.2.7.Final:osx-x86_64'
```

| Your System                    | Classifier to Use                |
| ------------------------------ | -------------------------------- |
| macOS Intel                    | `osx-x86_64`                     |
| macOS Apple Silicon (M1/M2/M3) | `osx-aarch_64`                   |
| Linux / Windows                | **Remove the entire dependency** |

#### For Linux or Windows Users

If you're **NOT on macOS**, remove or comment out the Netty macOS dependency in `build.gradle`:

```gradle
// --- DATA & CACHING ---
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
// Netty DNS resolver for macOS - REMOVE THIS LINE IF NOT ON macOS
// runtimeOnly 'io.netty:netty-resolver-dns-native-macos:4.2.7.Final:osx-x86_64'
```

The application will work correctly without it; Netty will use the default Java DNS resolver instead.

## 🔍 Code Quality

- **SonarQube Compliance** - Zero issues policy enforced
- **JUnit 5 & Mockito** - Comprehensive testing framework
- **ArchUnit** - Architectural integrity enforcement
- **100% Dependency Rule Compliance** - No layer leakage

## 📚 Documentation

- **OpenAPI/Swagger** - Full API documentation at `/api/v1/swagger-ui.html`
- **Javadoc** - Generate with: `./gradlew javadoc`

## 🚀 CI/CD

GitHub Actions pipeline included:

- Run on every push and PR to main/develop
- Execute full test suite
- Code quality verification
- Build optimization checks

## 📝 Git Workflow (GitFlow)

- **main** - Production-ready code
- **develop** - Integration branch
- **feat/\{context\}/\{description\}** - Feature branches
- **fix/\{context\}/\{description\}** - Bug fixes
- **hotfix/\{version\}** - Production hotfixes

## 🤝 Contributing

1. Create feature branch: `git checkout -b feat/user/login-logic`
2. Follow conventional commits: `feat(user): add login endpoint`
3. Push and create Pull Request
4. All tests must pass and code quality checks must succeed

## 📄 License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.
