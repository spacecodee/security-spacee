# **Project Context: Security Spacee – Java 25 & Spring Boot 4 Enterprise Template 🚀🔐**

Be extremely detailed with the file changes and the reason for the change.  
Add lots of emojis to the explanation in chat, but DO NOT add emojis to the code/comments unless explicitly requested.

## **🎯 1\. Project Overview**

This is a **Java 25 & Spring Boot 4** enterprise-grade application template. The core goal is to implement a strict *
*Hexagonal Architecture \+ Vertical Slicing** pattern. Key features include JWT authentication/authorization,
database-driven permissions, full i18n support, and secure token-based workflows like user registration and password
reset.

## **🏗️ 2\. Core Architectural Principles (MANDATORY)**

1. **Hexagonal Architecture (Four Layers):** The project is strictly divided into adapter, application, domain, and
   infrastructure layers within each feature slice.
2. **Vertical Slicing:** Code is organized by feature **Bounded Context** (e.g., user, auth, passwordreset), not by
   technical layer.
3. The Dependency Rule: All dependencies MUST point inwards.  
   Adapter & Infrastructure \-\> Application \-\> Domain. No exceptions.
4. **Purity of Inner Layers:**
    * The domain layer MUST have **zero** Spring or jakarta.persistence dependencies. It contains pure business logic
      and invariants.
    * The application layer should be as pure as possible. It orchestrates use cases and depends only on domain and its
      own ports. The only permitted framework dependencies are @Transactional and strictly necessary security
      interfaces (e.g., PasswordEncoder). **Framework-specific helpers like LocaleContextHolder or HttpServletRequest
      are strictly forbidden here.**
5. **Explicit Bean Configuration:**
    * Components bridging layers (Driven Adapters, Use Cases) MUST be configured via **@Bean methods** in
      \*BeanConfiguration classes (located in infrastructure/config).
    * Leaf nodes (@RestController implementation in adapter, @Repository in infrastructure) can use stereotype
      annotations (@Component, @RestController).
6. **Immutability First:** DTOs, Configuration Properties, and Domain Value Objects SHOULD be implemented as immutable
   records or classes with final fields.
7. **Manual Mapping Strategy:**
    * **Strict Rule:** Automatic mapping libraries (MapStruct, ModelMapper) are **FORBIDDEN**.
    * All mapping logic must be explicit, readable, and implemented in dedicated Mapper classes using strict Interface (
      I\*) \+ Implementation pattern.
8. **Event-Driven Communication:** For decoupling business processes (e.g., sending email after creation), use Spring's
   ApplicationEventPublisher. The Use Case publishes; a listener in a different context subscribes.
9. **Defense in Depth (Validation):**
    * **Adapter Layer:** Fail-fast using Jakarta Validation (@NotNull, @Email) on Request DTOs.
    * **Domain Layer:** Invariants enforced in Value Object constructors/factories.
10. **Timezone Standardization:**
    * **UTC Everywhere:** All dates MUST be stored in the database in UTC.
    * **Java Types:** Use Instant or OffsetDateTime for timestamps. **Avoid** LocalDateTime as it lacks timezone
      context.

## **📁 3\. Bounded Context Structure (Source of Truth)**

Each feature slice ({context}) MUST follow this structure exactly:

```
{context}/  
├── application/                     \# ORCHESTRATION LAYER: Coordinates data flow, no business rules here.  
│   ├── usecase/                     \# Implementation of Input Ports (Application Services).  
│   │   └── OrderCreator.java        \# Contains the flow logic (validation \-\> domain \-\> persistence).  
│   ├── port/                        \# HEXAGONAL PORTS: Contracts for interaction.  
│   │   ├── in/                      \# INPUT PORTS: Interfaces defining available Use Cases.  
│   │   │   └── ICreateOrderUseCase.java \# Interface starting with 'I'.  
│   │   └── out/                     \# OUTPUT PORTS: Interfaces for external services (Email, EventBus).  
│   │       └── IEmailServicePort.java   \# Interface starting with 'I' (Repository ports usually go in Domain).  
│   ├── command/                     \# INPUT DTOs: Data required to execute a Use Case.  
│   │   └── CreateOrderCommand.java  \# Agnostic of JSON/HTTP. Pure data holder.  
│   ├── response/                    \# OUTPUT DTOs: Data returned to the outside world.  
│   │   └── OrderResponse.java       \# Read-model, optimized for the client.  
│   └── mapper/                      \# MAPPING: Domain Objects \<-\> Response DTOs.  
│       ├── IOrderResponseMapper.java \# Interface definition (I\*).  
│       └── impl/  
│           └── OrderResponseMapperImpl.java  
│  
├── domain/                          \# CORE LAYER: Pure Java. No frameworks. The heart of the business.  
│   ├── model/                       \# AGGREGATES & ENTITIES: State and Behavior combined.  
│   │   └── Order.java               \# The Aggregate Root. Contains methods like complete(), cancel().  
│   ├── repository/                  \# REPOSITORY INTERFACES: Contracts for data persistence (DDD style).  
│   │   └── IOrderRepository.java    \# Defines methods like save(Order), findById(Id). Starts with 'I'.  
│   ├── valueobject/                 \# VALUE OBJECTS: Immutable, identified by value, self-validating.  
│   │   ├── Money.java  
│   │   └── ProductId.java  
│   ├── service/                     \# DOMAIN SERVICES: Logic involving multiple aggregates.  
│   │   └── OrderPriceCalculator.java  
│   ├── event/                       \# DOMAIN EVENTS: Something that happened in the past.  
│   │   └── OrderCreatedEvent.java  
│   └── exception/                   \# BUSINESS EXCEPTIONS: Meaningful errors.  
│       └── OrderNotFoundException.java  
│     
├── infrastructure/                  \# DRIVEN ADAPTERS & CONFIG: Technology details (SQL, Beans, etc.).  
│   ├── persistence/                 \# DATABASE ADAPTERS: Implementation of Repository Interfaces.  
│   │   ├── jpa/                     \# SPRING DATA & HIBERNATE specifics.  
│   │   │   ├── OrderEntity.java     \# Database Table representation (@Entity).  
│   │   │   └── SpringJpaOrderRepository.java \# Extends JpaRepository.  
│   │   ├── redis/                   \# \[NEW\] REDIS CACHE ADAPTERS.  
│   │   │   └── OrderRedisEntity.java \# Redis Hash representation (if needed).  
│   │   ├── mapper/                  \# MAPPING: Domain Objects \<-\> JPA Entities.  
│   │   │   ├── IOrderPersistenceMapper.java \# Interface (I\*).  
│   │   │   └── impl/  
│   │   │       └── OrderPersistenceMapperImpl.java  
│   │   └── OrderPersistenceAdapter.java \# The actual implementation of domain.repository.IOrderRepository.  
│   │  
│   ├── client/                      \# EXTERNAL APIs: RestTemplate/WebClient implementations.  
│   ├── config/                      \# FRAMEWORK CONFIG: Spring @Configuration classes.  
│   ├── security/                    \# SECURITY: Auth providers, Filters, JWT handling.  
│   └── util/                        \# TECHNICAL UTILS: Helpers for Dates, Strings, JSON (Non-business).  
│       ├── JsonUtils.java  
│       ├── DateUtils.java  
│       └── SecurityUtils.java  
│  
└── adapter/                         \# DRIVING ADAPTERS: Entry points (REST, gRPC, CLI).  
    ├── controller/                  \# WEB CONTROLLERS: Interface \+ Implementation pattern.  
    │   ├── IOrderController.java    \# INTERFACE: Contains Swagger (@Operation), Mappings (@PostMapping).  
    │   └── impl/  
    │       └── OrderControllerImpl.java \# IMPLEMENTATION: Logic execution (injects UseCase).  
    ├── request/                     \# REQUEST BODIES: JSON structure expected from clients.  
    │   └── CreateOrderRequest.java  \# Contains Jackson annotations (@JsonProperty).  
    ├── mapper/                      \# MAPPING: Request Body \<-\> Application Command.  
    │   ├── IOrderRestMapper.java    \# Interface (I\*).  
    │   └── impl/  
    │       └── OrderRestMapperImpl.java  
    └── response/                    \# RESPONSE BODIES: (Optional) If specific JSON format differs from App Response.
```

## **🧪 4\. Testing Strategy (Pyramid)**

* **Domain Layer:** Strictly **Unit Tests** using JUnit 5 and Mockito. No Spring Context. Fast execution. Focus on
  invariants and business logic.
* **Application Layer:** **Unit Tests** with mocked Ports (port/out). Verify orchestration and flow.
* **Infrastructure / Adapter Layer:** **Integration Tests** (@DataJpaTest, @WebMvcTest). Verify that the SQL queries
  work and that Controllers deserialized JSON correctly.
    * **Requirement:** These tests **MUST use H2 In-Memory Database**. Ensure application-test.yaml is configured with
      spring.datasource.url=jdbc:h2:mem:testdb.
* **Architecture Enforcement:** Use **ArchUnit** tests to strictly enforce the "Dependency Rule" and prevent layer
  leakage (e.g., ensuring no Controller calls a Repository directly).

## **🔐 5\. Key Design Decisions**

* **auth Context as Orchestrator:** Handles /login but delegates JWT creation to the jwttoken context.
* **jwttoken Context:** Sole owner of JWT keys and logic. Two validation modes: *Fast* (for filters, no DB) and
  *Strict* (for critical ops, DB check).
* **Thin Controllers with Interfaces:**
    * Controllers MUST follow the **Interface \+ Implementation** pattern.
    * **Interface (I\*Controller):** Holds all **Swagger/OpenAPI** annotations (@Operation, @ApiResponse) and Spring MVC
      mappings (@RequestMapping, @PostMapping). It serves as the API contract documentation.
    * **Implementation (\*ControllerImpl):** Contains the execution logic, calls the Use Case, and handles mapper calls.
      It is annotated with @RestController.
* **Anti-Service Locator:** Dependencies must be injected via **Constructor**. No ApplicationContext.getBean().
* **Caching (Redis Decorator Pattern):**
    * **Technology:** **Redis** is the mandatory caching store.
    * **Pattern:** Use the **Decorator Pattern**. Create a Cache Adapter that implements the Repository Interface (
      I\*Repository), wraps the actual JPA Adapter, and adds caching logic. This keeps the Domain and Use Cases
      completely agnostic of Redis.
* **Wither Pattern for Entities:** JPA Entities are immutable (no setters). Use withField() methods that return a new
  instance via @Builder(toBuilder=true).
* **Standardized Error Handling:** All exceptions must be handled by a Global Exception Handler that returns a standard
  Error Response (following **RFC 7807 Problem Details** recommended).
* **Mappers Locations:**
    * adapter/mapper: Request \-\> Command.
    * application/mapper: Domain \-\> Response.
    * infrastructure/persistence/mapper: Domain \<-\> Entity.

## **👁️ 6\. Observability & API Documentation (New Standards)**

* **Logging:**
    * Use **SLF4J** interfaces only.
    * **FORBIDDEN:** System.out.println or e.printStackTrace().
    * All logs MUST be structured (preferably JSON in production).
    * Logs MUST include a correlation ID (TraceID) to track requests across layers.
* **API Documentation (OpenAPI/Swagger):**
    * The **Adapter Layer (Controller Interfaces)** is the ONLY place allowed to have Swagger annotations (@Operation,
      @Schema).
    * Domain objects must NEVER be polluted with Swagger annotations.

## **🗄️ 7\. Database Management, Caching & Concurrency**

* **Schema Migration:**
    * Use **Flyway** for all database schema changes.
    * spring.jpa.hibernate.ddl-auto MUST be set to validate in production (never update or create).
* **Redis Caching Strategy:**
    * **Serialization:** MUST use GenericJackson2JsonRedisSerializer. Storing Java serialized objects (binary blobs) is
      **FORBIDDEN** as it prevents debugging and interoperability.
    * **TTL (Time-To-Live):** All cached entries MUST have an explicit expiration time.
    * **Keys:** Must follow a strict namespacing convention: app:{context}:{aggregate}:{id} (e.g., spacee:user:profile:
      123).
* **Concurrency Control (Optimistic Locking):**
    * All JPA Entities MUST include a @Version field to handle concurrent updates safely.
    * Use cases must handle OptimisticLockingFailureException gracefully.

## **🤖 8\. CI/CD & Code Quality Assurance (Strict)**

* **GitHub Actions (CI Pipeline):**
    * Every **Push** and **Pull Request** to main, develop, or feature branches MUST trigger a workflow.
    * The workflow MUST execute mvn test (or gradle test).
    * The build **MUST FAIL** if any unit or integration test fails.
* **SonarQube Compliance:**
    * **Zero Issues Policy:** The codebase MUST adhere to **100% of SonarQube rules**.
    * **Blockers/Criticals:** Absolutely forbidden.
    * **Code Smells:** Must be resolved immediately. Code should be clean and readable.
    * **Coverage:** Aim for high test coverage, but prioritize meaningful assertions over line hits.

## **🐳 9\. Containerization & Cloud Native Strategy**

* **Optimized Docker Builds (Production):**
    * **Multi-Stage Builds:** MUST use multi-stage Dockerfiles. Separate the build stage (Maven/Gradle) from the runtime
      stage to keep the image size minimal.
    * **Layered JARs:** Leverage Spring Boot's **Layered JAR** mode (-Djarmode=layertools). This splits dependencies and
      application code into different layers, drastically speeding up deployments when only code changes.
    * **Base Image:** Use **Distroless** (e.g., gcr.io/distroless/java25-debian12) or minimal Alpine images for the
      runtime stage to reduce attack surface.
* **Security Context:**
    * **Non-Root User:** The container **MUST NOT** run as root. Create a specific user/group (e.g., spring:spring) in
      the Dockerfile and switch to it using USER.
* **Development Workflow:**
    * Use docker-compose.yml only for infrastructure dependencies (Postgres, Mailpit, Redis).
    * The application itself should run locally in the IDE for faster debugging, connecting to the Dockerized
      infrastructure.
* **Resilience:**
    * **Graceful Shutdown:** Enable server.shutdown=graceful in Spring Boot to ensure active requests complete before
      the container stops (critical for Kubernetes rolling updates).
    * **Health Checks:** Dockerfiles MUST include a HEALTHCHECK instruction querying the Spring Boot Actuator
      /actuator/health endpoint.