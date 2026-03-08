# Finflow Transaction Platform

Production-grade Java 21 / Spring Boot transaction processing platform designed to demonstrate the best backend engineering practices for fintech-style systems.

## Why this repository exists

This project is intentionally optimized for modern backend hiring signals:

- REST-first API design with explicit versioning
- synchronous and asynchronous workflows in one system
- PostgreSQL + Flyway + JPA for transactional consistency
- Redis-backed caching strategy
- Kafka outbox publisher for reliable event delivery
- JWT resource server security model
- resilience patterns for external dependencies
- observability via Actuator, Micrometer, tracing hooks, and structured logs
- Docker Compose for local infrastructure
- Kubernetes manifests and CI pipeline examples

## Domain

The system processes outbound account-to-account transfers. A transfer request performs:

1. account validation
2. beneficiary screening
3. fraud scoring
4. durable transaction persistence
5. outbox event creation in the same database transaction
6. asynchronous publication to Kafka

## Architecture

The implementation is a **modular monolith** with clear package boundaries:

- `transfer` — transaction API, orchestration, persistence
- `outbox` — reliable event publication
- `security` — JWT resource server configuration
- `common` — exception and API error model
- `config` — cache, async, tracing, OpenAPI configuration

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## Tech stack

- Java 21
- Spring Boot 3.5.x dependency BOM
- Maven
- Spring MVC
- Spring Data JPA + Hibernate
- PostgreSQL
- Redis
- Kafka
- Flyway
- Spring Security OAuth2 Resource Server
- Resilience4j
- Micrometer / Actuator / OpenTelemetry bridge
- springdoc OpenAPI
- JUnit 5 / Mockito / Spring Boot Test

## Running locally

### Prerequisites

- JDK 21
- Maven 3.9+
- Docker Desktop or Docker Engine

### Start infrastructure

```bash
docker compose up -d
```

### Run the app

```bash
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd spring-boot:run
```

The API will be available at:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/actuator/health`
- `http://localhost:8080/actuator/prometheus`

## Build and test

```bash
./mvnw clean verify
```

## Example request

```bash
curl --request POST 'http://localhost:8080/api/v1/transfers'   --header 'Authorization: Bearer <jwt>'   --header 'Idempotency-Key: 0f7b5d95-6db0-4a72-8a92-60e8f9b2c001'   --header 'Content-Type: application/json'   --data '{
    "clientReference": "payroll-2026-03-07-0001",
    "sourceAccountId": "acct-usd-001",
    "destinationAccountId": "acct-usd-002",
    "beneficiaryId": "beneficiary-0042",
    "amount": 125.50,
    "currency": "USD"
  }'
```

## Security model

This project is configured as an OAuth2 resource server. For local development, a symmetric HS256 secret is configured in `application.yml`.

Expected scopes:

- `transfers.write`
- `transfers.read`

Expected JWT claim for multi-tenancy:

- `tenant_id`

## Reliability model

- request-level idempotency through `Idempotency-Key`
- database uniqueness on `(tenant_id, idempotency_key)`
- outbox pattern for reliable Kafka publication
- retry, circuit breaker, and bulkhead around fraud screening
- graceful retry scheduling for failed outbox events
- optimistic locking on transactional entities

## Local infrastructure

`docker-compose.yml` provides:

- PostgreSQL
- Redis
- Redpanda (Kafka-compatible broker)
- Redpanda Console

## Deployment assets

See `deploy/k8s/` for:

- deployment
- service
- ingress
- config map
- horizontal pod autoscaler

## CI/CD

See `.github/workflows/ci.yml` for a realistic pipeline with:

- build and test
- dependency and image scanning hooks
- container build
- deployment workflow gates

## Current implementation boundaries

This repository intentionally keeps one deployable application to maximize implementation focus and minimize local operational overhead. The module boundaries are suitable extraction seams for future service decomposition:

- transfer-command service
- transfer-query service
- outbox relay
- fraud adapter

## Recommended next steps

1. replace stub external clients with HTTP adapters + WireMock tests
2. add contract tests for produced Kafka events
3. add Redis-backed idempotency replay cache
4. add Testcontainers-backed PostgreSQL and Kafka integration suite
5. replace local JWT secret with external OIDC issuer
