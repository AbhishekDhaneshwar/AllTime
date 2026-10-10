# ADR-0007: Adopt Microservices from Day One

* **Status:** Accepted
* **Date:** 2026-10-10

## Context

AllTime is a social platform built around shareable notes and drawings, user profiles, public and private accounts, following, follow requests, likes, comments, feeds, and blocking.

The backend will use Java 21 and Spring Boot, with MySQL for structured data and object storage for media. The platform is intended to evolve toward deployment on AWS.

Two architectural approaches were considered:

1. Start with a modular monolith and extract services as the product grows.
2. Establish independently deployable microservices from the beginning.

A modular monolith would reduce initial operational complexity. However, it would require later extraction of service boundaries, data ownership, deployment responsibilities, and inter-service communication.

The project has explicitly selected the second approach. The architecture and implementation must reflect this decision.

## Decision

AllTime will use a microservices architecture from day one.

Each business service will own a distinct capability, its data, and its business rules. Services will communicate through documented APIs or asynchronous events rather than directly accessing each other's databases.

The initial service boundaries are:

### 1. Identity Service

Owns phone-number registration, OTP verification, credential management, authentication, access tokens, refresh tokens, and authentication-related security records.

### 2. Social Service

Owns user profiles, public/private account settings, follow relationships, follow requests, and blocking.

Authentication credentials remain owned by the Identity Service.

### 3. Content Service

Owns notes, posts, doodle metadata, content visibility, ownership references, and media metadata. Binary media will be stored in object storage.

### 4. Engagement Service

Owns likes and comments, referencing content through stable identifiers.

### 5. Feed Service

Owns feed-specific state and retrieval logic. It may maintain derived feed data but will not become the authoritative owner of content or social relationships.

### 6. API Gateway

Provides a common entry point and routes client requests to the appropriate backend services. Business authorization remains the responsibility of the services that own the resources.

## Data Ownership

Each business service will have its own logical database and exclusive write ownership.

Initially, the logical databases may share one MySQL server to keep costs and operational overhead manageable. Database credentials and permissions will be scoped to the owning service.

Cross-service database joins and direct access to another service's tables are prohibited. Integration will use APIs or asynchronous messaging.

The Feed Service may use derived or materialized data for performance, provided that ownership and synchronization are explicit.

## Communication Strategy

REST will be the initial mechanism for synchronous communication.

An asynchronous message broker may be introduced when justified by background processing, event-driven feed updates, notifications, or other integration requirements.

Service discovery, centralized configuration, distributed tracing, and resilience mechanisms will be introduced incrementally rather than treated as prerequisites for implementing the first service.

## Security Considerations

The Identity Service will authenticate users and issue access and refresh tokens.

Each service must validate authentication information and enforce authorization for its resources. The Gateway alone will not be treated as a complete security boundary.

Private-account visibility, approved follow requests, blocking, and content ownership must be enforced consistently across content retrieval and interaction workflows.

Credentials, OTPs, signing keys, and other secrets must be protected and excluded from source control.

## Consequences

### Positive

* Clear business boundaries and data ownership from the beginning.
* Services can be built, tested, and deployed independently.
* Independent evolution and scaling of services where justified.
* Practical alignment with the intended AWS and Spring Boot microservices architecture.
* Reduced risk of postponing service-boundary and integration decisions until after substantial implementation.

### Negative

* Greater initial setup and deployment complexity than a modular monolith.
* More complicated local development and integration testing.
* Network failures, latency, and partial service outages must be handled.
* Cross-service workflows require explicit consistency and failure-handling strategies.
* Observability, API contracts, authentication, and authorization require deliberate design.

### Mitigations

* Start with five business services and one gateway; avoid premature service proliferation.
* Allow the logical databases to share one MySQL instance initially.
* Use REST for initial synchronous interactions.
* Add messaging and other infrastructure only when a concrete requirement justifies them.
* Establish automated tests, health checks, structured logging, and consistent error handling early.
* Document significant follow-up decisions in additional ADRs.

## Implementation Implications

The architecture baseline must define the selected services, their responsibilities, data ownership, communication rules, and deployment expectations before application coding starts.

The initial implementation will establish repository structure and engineering conventions, then build the Identity Service foundation and proceed through the other services incrementally.

A modular monolith is not the chosen initial architecture. The project will not defer the microservices decision until after the first business features have been implemented.

## References

* `docs/architecture-baseline.md`
* `docs/product-requirements.md`
* `docs/api-conventions.md`
