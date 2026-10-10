# AllTime

AllTime is a social platform concept for Indian youth and young adults, with a distinctive focus on written notes, handwritten-style content, drawings, and doodles shared with a user's community.

> **Project status:** Architecture and requirements are at the proposed-baseline stage. This repository is not yet a production-ready application. The initial work is documentation-first; implementation begins after the baseline has been reviewed and recorded.

## Product direction

The initial MVP is expected to cover:

- Phone-number registration with OTP verification and password-based login.
- User profiles with public/private account settings.
- Following, follow requests, unfollowing, and blocking.
- Text and drawing-based notes, including editing and deleting one's own notes.
- A paginated home feed, likes, comments, and basic reporting.
- Authorization, input validation, tests, health checks, and operational logging.

Direct messages, stories, video streaming, and advanced recommendation systems are outside the initial MVP.

## Planned technology direction

- **Android client:** Java using Android Studio.
- **Backend:** Java 21 and Spring Boot.
- **Build:** Maven Wrapper.
- **API:** Versioned REST endpoints with JSON.
- **Database:** MySQL with versioned schema migrations.
- **Media:** Object storage for drawings and other media; metadata and object keys in MySQL.
- **Security:** Spring Security, password hashing, OTP safeguards, short-lived access tokens, and refresh-token rotation/revocation.
- **Deployment direction:** Docker and AWS; exact runtime and AWS services are not yet decided.

Exact dependency versions and external providers will be selected when the foundation is implemented and will be kept compatible with Java 21.

## Architecture principle

Start with a **modular Spring Boot backend** with explicit domain boundaries. Do not split every domain into a separately deployed microservice before there is a concrete reason. Service extraction can happen later when scaling, deployment independence, fault isolation, or a validated learning objective justifies it.

## Repository map

```text
AllTime/
├── README.md
├── CONTRIBUTING.md
├── .gitignore
└── docs/
    ├── product-requirements.md
    ├── architecture-baseline.md
    ├── api-conventions.md
    └── adr/
        ├── 0001-backend-first.md
        ├── 0002-java-spring-boot.md
        ├── 0003-rest-api-conventions.md
        ├── 0004-follow-privacy-model.md
        ├── 0005-mysql-data-ownership.md
        ├── 0006-drawing-media-storage.md
        ├── 0007-modular-backend-evolution.md
        └── 0008-phone-otp-jwt-authentication.md
```

The `backend/` application directory will be added in the implementation phase after the baseline is reviewed.

## Documentation guide

- [Product requirements](docs/product-requirements.md): MVP scope, user journeys, functional requirements, quality requirements, and open product questions.
- [Architecture baseline](docs/architecture-baseline.md): proposed domains, privacy rules, data/media direction, operations, and open decisions.
- [API conventions](docs/api-conventions.md): draft REST contract and standards for requests, responses, errors, and pagination.
- [Architecture Decision Records](docs/adr/): the context and trade-offs behind major technical decisions. All ADRs currently remain **Proposed** until reviewed.

## Suggested first workflow

1. Review the requirements and architecture baseline.
2. Resolve or explicitly defer decisions that affect the first implementation.
3. Update ADR statuses only after agreeing to the decisions.
4. Commit the reviewed documentation to Git.
5. Create the backend in a feature branch and add a repeatable build, tests, configuration, and database migrations.

See [CONTRIBUTING.md](CONTRIBUTING.md) for the proposed collaboration workflow.

## Safety and launch readiness

The intended audience may include people aged 16–17. Before a public launch, assess applicable Indian privacy, age-related, child-safety, moderation, and data-retention obligations with qualified guidance. Do not treat the current notes as legal advice or a completed compliance assessment.
