# ADR 0003: Use versioned REST APIs with JSON and explicit DTOs

- **Status:** Proposed
- **Date:** 10 October 2026
- **Decision owners:** AllTime project maintainers

## Context

The Android client and backend need an explicit, testable contract. Authentication, profiles, social relationships, notes, feed, and engagement are resource-oriented operations that can be represented cleanly over HTTP.

## Decision

Use REST-style HTTP APIs under `/api/v1`, JSON request/response bodies, explicit request and response DTOs, Bean Validation, centralized error handling, OpenAPI documentation, and standard HTTP status semantics. Keep persistence entities internal. Document pagination and authorization behavior as part of each relevant endpoint contract.

## Alternatives considered

- **GraphQL:** flexible client-driven selection, but adds schema/resolver complexity not justified for the initial MVP.
- **gRPC:** strong service-to-service contracts, but is not the simplest first contract for a native Android client and the initial modular backend.
- **Return JPA entities directly:** quick for prototypes, but couples public API shape to persistence and risks accidental field exposure.
- **Unversioned ad hoc endpoints:** less ceremony initially, but harder to evolve without breaking clients.

## Consequences

### Positive

- Client/backend interactions are explicit and easy to test with standard HTTP tooling.
- DTOs protect the API from accidental database-schema changes.
- Consistent errors and API docs reduce client ambiguity.

### Negative / trade-offs

- DTO mapping creates additional code that must be maintained.
- Versioning policy and backward compatibility need discipline.
- REST is not optimal for every future real-time or high-volume use case; specialized communication can be added if justified.

## Revisit when

Revisit the API style if validated client requirements or service-to-service communication needs materially outgrow this approach. Do not add another protocol only for novelty.
