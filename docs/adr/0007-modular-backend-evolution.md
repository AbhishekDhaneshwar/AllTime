# ADR 0007: Start with a modular backend and extract services only when justified

- **Status:** Proposed
- **Date:** 10 October 2026
- **Decision owners:** AllTime project maintainers

## Context

AllTime has distinguishable domains—identity, profiles, social graph, notes/media, engagement/moderation, and feed—but the MVP's traffic, deployment needs, and team size are not yet known. Deploying multiple services immediately would introduce network failures, distributed tracing, inter-service authentication, more deployment units, and data consistency questions.

## Decision

Begin with a modular Spring Boot backend in one deployable application. Keep domain boundaries explicit, assign business-rule/write ownership, and test modules through stable interfaces. Extract a domain into a separately deployed service only when a measurable need exists, such as independent deployment, meaningful scale differences, fault isolation, or a carefully defined learning objective.

## Alternatives considered

- **Independent microservice for each domain from day one:** provides deployment separation but incurs substantial operational and distributed-systems complexity before usage patterns are known.
- **Unstructured monolith:** easier to start but allows domain rules to leak across modules and makes later extraction difficult.
- **Serverless functions per endpoint:** may fit some workloads but spreads the domain across deployment units and does not currently simplify the core product enough to justify it.

## Consequences

### Positive

- One application is easier to run, test, debug, and deploy while the product is small.
- Domain boundaries can be learned and validated before they become network boundaries.
- Infrastructure costs and operational burden are lower than running many services prematurely.

### Negative / trade-offs

- Independent scaling and deployments are not available until extraction.
- A shared database and process need discipline to prevent cross-domain coupling.
- Developers must resist both extremes: an unstructured monolith and premature service sprawl.

## Extraction checklist

Before extracting a domain, document:

- The measurable reason for extraction and the success criterion.
- Its data ownership and migration/transition plan.
- API/event contracts, timeouts, retries, idempotency, and failure behavior.
- Authentication and authorization between services.
- Observability, deployment, rollback, and operational ownership.
- Added infrastructure and on-call cost.

## Revisit when

Revisit after production-like workload tests or when independent deployment, isolation, or scale requirements can be demonstrated. The user's learning goals can inform practice, but should not be confused with an automatic production requirement to split services.
