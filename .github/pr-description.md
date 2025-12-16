# feat(shared): Add i18n Configuration & Fix Mockito Warnings

## 📋 Description

This PR adds comprehensive internationalization (i18n) support to the Security Spacee application with proper Spring MessageSource configuration, and fixes Mockito dynamic agent loading warnings for Java 25 compatibility.

### Changes Include:

#### 1️⃣ i18n Configuration (feat/shared/i18n-configuration)
- **InternationalizationConfig**: Spring configuration class in `shared/config` package
  - ResourceBundleMessageSource for message loading
  - AcceptHeaderLocaleResolver for HTTP Accept-Language header detection
  - LocaleChangeInterceptor for query parameter locale override (`?lang=es`)
  - LocalValidatorFactoryBean integration for Jakarta validation i18n support

- **Message Files**: Converted YAML to .properties format (Spring Boot requirement)
  - `messages_en.properties`: 52 English message keys
  - `messages_es.properties`: 52 Spanish message keys (ES/ES locale)
  - Support for parameterized messages with MessageFormat placeholders
  - Removed orphaned message key (`user.exception.not_found.id` - no UserNotFoundException)

- **Message Categories**:
  - User registration/profile management
  - Email templates (welcome, password reset, email verification)
  - Validation errors (username, email, password, profile fields)
  - Business exceptions (duplicate username/email, invalid data, etc.)
  - Global error/validation messages

- **Test Coverage**: Comprehensive InternationalizationConfigTest with 7 scenarios
  - MessageSource bean injection verification
  - English/Spanish message resolution
  - Parameterized message handling
  - Fallback behavior validation
  - Nested message key structure verification

#### 2️⃣ Mockito Java Agent Fix (fix/test/mockito-warnings)
- Added `-XX:+EnableDynamicAgentLoading` JVM flag to test task
- Resolves Mockito inline-mock-maker self-attaching warnings
- Prevents future JDK deprecation warnings
- Maintains Java 25 compatibility with Mockito 5.x

## 🎯 Architecture Decision

**Placement**: i18n configuration placed in `shared/config` package (not a bounded context)

**Rationale**:
- i18n is a **cross-cutting concern** used by multiple bounded contexts (user, auth, passwordreset, etc.)
- Follows **Hexagonal Architecture** - shared infrastructure concerns in shared/config
- Avoids duplication and ensures consistent message handling across contexts

## 🔄 Type of Change
- [x] New feature (i18n framework)
- [x] Bug fix (Mockito warnings)
- [ ] Breaking change
- [ ] Documentation update

## ✅ Testing
- [x] All unit tests passing (7/7 in InternationalizationConfigTest)
- [x] All integration tests passing
- [x] No Mockito warnings during test execution
- [x] MessageSource loads correctly in Spring context
- [x] Both EN and ES locales working
- [x] Parameterized messages resolved correctly
- [x] HTTP Accept-Language detection working
- [x] Query parameter locale override working

## 📝 Files Modified
- `build.gradle`: Added Mockito Java Agent configuration
- `src/main/java/.../shared/config/InternationalizationConfig.java` (NEW)
- `src/main/resources/i18n/messages_en.properties` (NEW - converted from YAML)
- `src/main/resources/i18n/messages_es.properties` (NEW - converted from YAML)
- `src/test/java/.../shared/config/InternationalizationConfigTest.java` (NEW)
- Deleted: `messages.yaml`, `messages_en.yaml`, `messages_es.yaml` (YAML not supported)

## 🔗 References
- [MessageSource Spring Documentation](https://docs.spring.io/spring-framework/reference/core/beans/context-functionality-messagesource.html)
- [LocaleResolver Spring Documentation](https://docs.spring.io/spring-framework/reference/web/webflux/httprequests.html#webflux-locale)
- [Mockito Java Agent](https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3)
- [JEP 451: Prepare to Disallow the Dynamic Loading of Agents](https://openjdk.org/jeps/451)

## 🚀 Next Steps
- Merge to `develop` branch
- Integration testing in staging environment
- Consider adding more locales (FR, DE, etc.) if needed

## 📌 Notes
- All placeholders follow MessageFormat convention: `{0}`, `{1}`, `{2}` (sequential from zero)
- Messages use camelCase keys (e.g., `user.registration.success`)
- No external mapping libraries used (follows project architecture)
- Locale fallback disabled (explicit translations required)

---

**Commits**:
- `feat(shared): configure i18n with MessageSource and locale resolution`
- `fix(test): suppress Mockito dynamic agent loading warnings in Java 25`
