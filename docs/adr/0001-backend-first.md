# ADR 0001: Build and validate the backend before Android integration

- **Status:** Proposed
- **Date:** 10 October 2026
- **Decision owners:** AllTime project maintainers

## Context

AllTime will have a native Android client written in Java and a Java/Spring Boot backend. Building both sides simultaneously before the API and authorization rules are understood can create rework, duplicate business rules in the client, and obscure backend security defects.

## Decision

Establish the initial backend foundation and document its API contracts before integrating the Android app. Develop vertical slices with automated tests and API examples. Android integration can begin once the relevant endpoints and error/authentication behavior are sufficiently stable.

## Alternatives considered

- **Build Android and backend together from day one:** gives early visual feedback but couples client progress to unsettled contracts.
- **Build the complete backend before any client work:** reduces contract churn but delays usability feedback. This is not required; integrate progressively after the first stable slices.
- **Prototype with a hosted backend:** may speed a demo but does not align as well with the project's Spring Boot learning and architecture goals.

## Consequences

### Positive

- API contracts, validation, authorization, and persistence can be tested independently.
- Backend security does not depend on client behavior.
- A documented API can support future clients.

### Negative / trade-offs

- The product's visual and interaction design receives feedback later than in a parallel build.
- API examples and test clients are needed to validate user journeys before the Android UI is ready.

## Revisit when

Revisit the sequencing if user testing requires an early clickable prototype. A prototype should not be mistaken for the production backend or bypass established access-control rules.
