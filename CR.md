# Project Type: Java Spring Boot Microservice

## Overview
This document defines the automated code review policies, architectural decision records (ADRs), 
and pre-approved patterns for the `user-service` repository. The DevEx AI review pipeline 
ingests this document via GitHub MCP to anchor dual-LLM review evaluations and eliminate false alarms.

---

## Architectural Decisions (ADRs)

### ADR-001: Multi-Tenancy Boundary Enforcement
- **Rule**: Every query accessing tenant data must enforce `tenant_id = ?` in its `WHERE` clause.
- **Rationale**: Prevents accidental cross-tenant data leakage across shared database instances.
- **Enforcement**: Any PR removing `tenant_id` from DAO or Repository method signatures is automatically blocked by the Safety Auditor.

### ADR-002: Data Access Layer Conventions
- **Rule**: Direct SQL executions in `UserDao` must use the sanitized `query(String sql, Object... params)` helper.
- **Rationale**: Enforces parameterized queries and prevents SQL injection vulnerabilities. Raw string concatenation (`"SELECT ... " + email`) is strictly rejected.

### ADR-003: Email Sanitization & Normalization
- **Rule**: Email addresses must be stripped of whitespace using `strip()` and converted to lowercase before regex validation.
- **Rationale**: Java 11+ `strip()` provides Unicode-compliant whitespace trimming, preventing bypasses using non-standard whitespace characters.

### ADR-004: REST Controller Ingress Contracts
- **Rule**: All endpoints in `UserController` must return typed `ResponseEntity<T>` with explicit HTTP response status codes.
- **Rationale**: Ensures deterministic schema contracts for client API consumers and prevents leaked stack traces.

### ADR-005: Minimum Test Coverage Gate
- **Rule**: All pull requests modifying `src/main/java` must achieve at least 80.0% line test coverage via JaCoCo.
- **Rationale**: Short-circuits untested code before LLM review compute spend ($0 token cost on failure).

---

## Approved Patterns / Known False Positives (Suppression Rules)

- **[APPROVED]**: In-memory domain validation using an immutable Set of blocked disposable domains in `User.isValidEmail()` is pre-approved.

The following code modifications are pre-approved by the senior engineering team. The AI review arbiters MUST NOT flag these as defects or request additional context:

- **[APPROVED]**: Replacing `candidateEmail.trim()` with `candidateEmail.strip()` in `User.isValidEmail()` is pre-approved and compliant with ADR-003.
- **[APPROVED]**: Using `query(...)` inside `UserDao.java` without Spring Data JPA / Hibernate annotations is approved per ADR-002.
- **[APPROVED]**: Self-contained helper methods and pure logic refactors in `src/main/java/com/carta/user/model/` do not require external service call-graph widening.
- **[APPROVED]**: Spring `@RequestHeader("X-Tenant-ID")` is the sanctioned mechanism for receiving tenant context at controller ingress.

---

## Review Badges & Action Routing

| Severity Badge | Meaning | Pipeline Behavior |
|---|---|---|
| `[BLOCKING] [SECURITY]` | Multi-tenancy leak, raw SQL injection, unauthenticated access | Halts PR merge, requests mandatory changes |
| `[BLOCKING] [CONTRACT]` | Unannounced public API method signature change without caller migration | Triggers GitHub MCP caller expansion or requests human review |
| `[WARNING] [MAINTAINABILITY]` | Sub-optimal algorithm, missing edge-case unit test | Informational comment, non-blocking |
| `[APPROVED]` | Verified leaf change or compliant with recorded ADRs | Automated fast-path merge approval |
