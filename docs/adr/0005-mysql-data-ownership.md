# ADR 0005: Use MySQL with explicit domain ownership and versioned migrations

- **Status:** Proposed
- **Date:** 10 October 2026
- **Decision owners:** AllTime project maintainers

## Context

Accounts, profiles, follows, notes, likes, comments, and reports contain relational data with uniqueness and relationship constraints. A relational database is a practical starting point for these workflows. Even in a single deployment, unclear write ownership can create tightly coupled modules.

## Decision

Use MySQL for structured application data and manage schema changes through version-controlled database migrations. Initially, modules may use one MySQL database, but each domain owns its write operations and invariants. Other domains should call domain-level services/interfaces rather than directly modifying another domain's tables. Use database constraints and indexes for durable invariants and expected query patterns.

## Alternatives considered

- **PostgreSQL:** also a strong relational option; not chosen for the initial baseline because MySQL is the selected learning/project direction. Re-evaluate if specific features require it.
- **Document database as the primary store:** flexible for nested content but less suitable as the default for relationship and uniqueness constraints in the initial product.
- **Separate database per domain immediately:** clearer physical isolation but adds deployment, migration, consistency, and operational complexity before it is justified.
- **Create tables automatically without committed migrations:** convenient locally but makes fresh setup, review, and production upgrades less reproducible.

## Consequences

### Positive

- Supports transactional relationships and strong integrity constraints.
- Fresh environments and CI can create schemas reproducibly from migrations.
- Explicit write ownership helps a modular monolith evolve toward independently owned services if needed.

### Negative / trade-offs

- A shared database still creates some physical coupling between modules.
- Cross-domain reads and transactions need deliberate design.
- Migration discipline and query/index maintenance add development work.

## Implementation requirements

- Store schema migrations with the backend and validate them in automated tests.
- Normalize phone numbers and usernames under explicitly documented rules before relying on uniqueness constraints.
- Add unique constraints for usernames, follow pairs, and likes where appropriate.
- Avoid storing large drawing binaries directly in ordinary relational rows.
- Never treat database access as a substitute for authorization checks.

## Revisit when

Revisit the database technology or physical database boundaries if measured scaling, availability, isolation, or feature needs provide evidence that a change is beneficial.
