# Domain Events Pattern: Direct Builder vs Mapper Abstraction

## Decision Summary

**Domain Events MUST be created directly using `.builder()` in Use Cases without intermediate Mapper abstraction layers.
**

## Rationale

### 1. Domain Events Are NOT Transformation Objects

**Mappers are appropriate for:**

- ✅ Domain → Response DTO (output transformation for API clients)
- ✅ Domain → Entity (transformation to persistence layer)
- ✅ Request DTO → Command (input transformation from API clients)
- ✅ Complex aggregates → Event (when combining data from multiple aggregates)

**Domain Events are NOT appropriate candidates for Mapper abstraction because:**

- ❌ Events are **Domain Objects**, not external DTOs
- ❌ They remain **within the bounded context** and don't cross layer boundaries
- ❌ **No transformation occurs** - field values are passed directly from already-extracted Use Case data
- ❌ The mapper would add **unnecessary indirection** with zero value

### 2. Events are Created Within the Same Context

```java
// ✅ CORRECT: Builder directly in Use Case (following project standards)
LoginSuccessEvent event = LoginSuccessEvent.builder()
                .userId(authResult.getUserId())        // data already extracted
                .username(authResult.getUsername())    // in same use case
                .email(authResult.getEmail())
                .userType(authResult.getUserType())
                .loginTimestamp(Instant.now())
                .ipAddress(command.ipAddress())
                .userAgent(command.userAgent())
                .assignedRoles(authResult.getAssignedRoles())
                .build();

eventPublisher.

publishEvent(event);

// ❌ INCORRECT: Unnecessary mapper layer (adds no value)
var event = this.eventMapper.toEvent(authResult);  // extra indirection
eventPublisher.

publishEvent(event);
```

### 3. Mapper Overhead Without Benefits

| Aspect                    | With Mapper                    | Direct Builder            | Winner     |
|---------------------------|--------------------------------|---------------------------|------------|
| **Clarity**               | Hides event creation details   | Transparent field mapping | Direct     |
| **Maintainability**       | Extra file to manage           | Single place              | Direct     |
| **Flexibility**           | Decoupled from event structure | Tightly coupled           | Mapper     |
| **Complexity**            | Extra indirection layer        | Simple and direct         | Direct     |
| **Actual Transformation** | None (just field passing)      | None (just field passing) | Tie        |
| **Cross-Context Usage**   | ❌ Events stay in context       | ❌ Events stay in context  | Irrelevant |

**Verdict:** For within-context event creation, **Direct Builder is superior** because there's no transformation logic
to abstract.

## When Mappers WOULD Be Appropriate for Events

Mappers for events should only be introduced if:

1. **Complex Cross-Aggregate Transformation**
    - Event requires combining data from multiple aggregates
    - Logic: `Aggregate A + Aggregate B + Context Data → Event`

   ```java
   // Example: OrderShippedEvent combines Order + Shipment + Warehouse data
   public OrderShippedEvent toEvent(Order order, Shipment shipment, Warehouse warehouse) {
       return OrderShippedEvent.builder()
           .orderId(order.getOrderId())
           .shipmentId(shipment.getShipmentId())
           .warehouseLocation(warehouse.getLocation())
           .estimatedDelivery(this.calculateDelivery(shipment, warehouse))  // logic
           .build();
   }
   ```

2. **Complex Business Logic During Event Creation**
    - Conditional field mapping
    - Calculated fields
    - Enum transformations
    - State machine outputs

   ```java
   // Example: Event state depends on business rules
   public AccountStatusChangedEvent toEvent(Account account, AuditLog log) {
       AccountStatus status = this.determineStatus(account, log);  // logic
       
       return AccountStatusChangedEvent.builder()
           .accountId(account.getId())
           .newStatus(status)
           .reason(this.getReason(account))  // logic
           .timestamp(Instant.now())
           .build();
   }
   ```

3. **Event Creation from External DTO**
    - Event must be constructed from API request or third-party system
    - Transformation: `ExternalDTO → Domain Event`

## Current Project Pattern Verification

### ✅ Correct Patterns in Existing Code

**auth/application/usecase/LoginUseCase.java**

```java
LoginSuccessEvent event = LoginSuccessEvent.builder()
        .userId(authResult.getUserId())
        .username(authResult.getUsername())
        .email(authResult.getEmail())
        // ... direct field mapping
        .build();

eventPublisher.

publishEvent(event);
```

**jwttoken/application/usecase/IssueTokenUseCase.java**

```java
TokensIssuedEvent event = TokensIssuedEvent.builder()
        .userId(authResult.getUserId())
        .tokenType(TokenType.BEARER)
        .issuedAt(Instant.now())
        // ... direct field mapping
        .build();

eventPublisher.

publishEvent(event);
```

### ✅ Session Context (After Reversion)

**session/application/usecase/SessionCreator.java**

```java
SessionCreatedEvent event = SessionCreatedEvent.builder()
        .sessionId(savedSession.getSessionId())
        .sessionToken(savedSession.getSessionToken())
        .userId(savedSession.getUserId())
        .createdAt(savedSession.getMetadata().getCreatedAt())
        .expiresAt(savedSession.getMetadata().getExpiresAt())
        .build();

eventPublisher.

publishEvent(event);
```

## Design Principle: Separation of Concerns

The purpose of Mappers is to **encapsulate transformation logic at layer boundaries**:

```
┌─────────────────────────────────────────────────────────────┐
│                      Use Case Layer                         │
│  (Orchestration - zero transformation logic)               │
└────┬──────────────────────────────────────┬────────────────┘
     │                                      │
  Mapper                                   Direct
  ↓ (Transformation)                       Builder
┌──────────────────┐                      ↓
│  Response DTO    │              ┌──────────────────┐
│  (crosses out)   │              │ Domain Event     │
└──────────────────┘              │ (stays in domain)│
                                   └──────────────────┘
     ❌ Needs transformation            ✅ No transformation
     ❌ Crosses context boundary        ❌ Stays in bounded context
```

## Code Guidelines

### ✅ DO

- Create events directly using `.builder()` in Use Cases
- Place event creation immediately after persistence (if creating event from aggregate)
- Use descriptive variable names: `var event = SessionCreatedEvent.builder()`
- Extract complex logic into domain methods before event creation

### ❌ DON'T

- Create Event Mappers for simple field copying
- Inject Event Mappers into Use Cases (adds layer of indirection)
- Use mapper beans for event construction within same bounded context
- Hide event field mappings behind mapper layer

## Example Anti-Pattern (Avoid)

```java
// ❌ ANTI-PATTERN: Unnecessary mapper for no-transformation event
public class SessionCreator implements ICreateSessionUseCase {
    private final ISessionCreatedEventMapper eventMapper;  // ❌ Unnecessary

    public void execute(CreateSessionCommand command) {
        // ... session creation logic ...

        var event = this.eventMapper.toEvent(savedSession);  // ❌ Extra indirection
        eventPublisher.publishEvent(event);
    }
}

// ISessionCreatedEventMapper.java (❌ Useless)
public interface ISessionCreatedEventMapper {
    SessionCreatedEvent toEvent(Session session);
}

// SessionCreatedEventMapperImpl.java (❌ Just passes fields)
public class SessionCreatedEventMapperImpl implements ISessionCreatedEventMapper {
    public SessionCreatedEvent toEvent(Session session) {
        return SessionCreatedEvent.builder()
                .sessionId(session.getSessionId())        // ❌ No transformation
                .sessionToken(session.getSessionToken())  // ❌ Just field copy
                .userId(session.getUserId())
                .createdAt(session.getMetadata().getCreatedAt())
                .expiresAt(session.getMetadata().getExpiresAt())
                .build();
    }
}
```

## Summary

| Aspect                        | Direct Builder          | Mapper           | Recommendation |
|-------------------------------|-------------------------|------------------|----------------|
| **Within-context events**     | ✅ Simple, clear         | ❌ Overhead       | Direct Builder |
| **Cross-aggregate events**    | ⚠️ Complex logic        | ✅ Encapsulation  | Mapper         |
| **Events from external DTOs** | ❌ Mixed concerns        | ✅ Clean boundary | Mapper         |
| **Project consistency**       | ✅ Matches auth/jwttoken | ❌ Inconsistent   | Direct Builder |
| **Maintenance burden**        | ✅ Minimal               | ⚠️ Extra files   | Direct Builder |

**Final Rule:** Unless your event creation involves **actual transformation logic or data from multiple aggregates**,
use Direct Builder in the Use Case. Mappers are for translating between boundaries, not for passing fields within the
same context.
