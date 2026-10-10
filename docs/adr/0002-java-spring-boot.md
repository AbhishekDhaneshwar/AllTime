# ADR 0002: Use Java 21, Spring Boot, and the Maven Wrapper for the backend

- **Status:** Proposed
- **Date:** 10 October 2026
- **Decision owners:** AllTime project maintainers

## Context

The project owner is learning and applying Java-based backend development and wants a maintainable foundation that supports REST APIs, validation, security, persistence, tests, and future cloud deployment.

## Decision

Use Java 21 for the initial backend, Spring Boot for the application framework, and the Maven Wrapper as the project's standard build entry point. Select a Spring Boot release compatible with Java 21 and the chosen dependency set when the backend project is generated. Pin and maintain dependency versions in source control and automate build/test validation.

## Alternatives considered

- **A different JVM framework:** technically viable, but would not match the selected Spring Boot learning and project direction.
- **A different language/runtime:** can be appropriate for other teams, but would split the current learning and implementation focus.
- **Rely on globally installed Maven:** makes builds depend on each developer's machine and Maven version; rejected in favor of the Wrapper.
- **Use the newest framework release without compatibility checks:** rejected; versions should be selected deliberately and validated against Java 21 and required libraries.

## Consequences

### Positive

- Provides conventions and integrations for REST, security, validation, data access, health checks, and testing.
- The Wrapper makes builds more reproducible across developer machines and CI.
- Java 21 is the baseline for source/target compatibility and local runtime documentation.

### Negative / trade-offs

- Dependency updates and compatibility need ongoing attention.
- Spring Boot has a learning curve and can make small features appear more framework-heavy than they are.

## Implementation notes

- Commit the Maven Wrapper scripts and wrapper configuration needed by the generated project. Do not assume every wrapper JAR must be ignored; follow the generated wrapper's standard layout and project policy.
- Add a documented command for clean build and tests.
- Keep environment secrets out of application YAML committed to Git.

## Revisit when

Revisit the Java baseline when a supported-runtime change is necessary or when requirements demonstrate a concrete benefit from another runtime. Framework version changes should be handled as dependency maintenance, not by silently changing the language baseline.
