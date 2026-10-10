# AllTime — Architecture Baseline

**Status:** Proposed — pending review and recording  
**Version:** 0.1  
**Last updated:** 10 October 2026

## 1. Product vision

AllTime is a social platform for Indian youth and young adults, primarily aged 16–35, with potential relevance up to age 40.

Its signature feature is the ability to create written notes, handwritten notes, drawings, and doodles and share them with an authorized audience.

## 2. MVP scope

The initial product includes:

- Account registration, phone verification, login, and profile management.
- Public and private profiles.
- Following, follow requests, unfollowing, and blocking.
- Written and drawn notes.
- Editing and deleting one's own notes.
- A paginated home feed.
- Likes and comments.
- Basic content and account reporting.
- Authentication, authorization, validation, logging, and health checks.

Direct messaging, stories, advanced recommendations, and video streaming are excluded from the initial MVP.

## 3. Technology direction

- **Android client:** Java and Android Studio.
- **Backend:** Java 21 and Spring Boot.
- **Build:** Maven Wrapper.
- **API style:** Versioned REST APIs using JSON.
- **Database:** MySQL.
- **Security:** Spring Security, phone verification, password hashing, JWT-style access tokens, and a refresh-token strategy with rotation and revocation.
- **Media:** Object storage for drawing images; relational metadata in MySQL.
- **Containers:** Docker.
- **Cloud target:** AWS, with runtime and service choices to be confirmed.

Exact framework versions and external providers will be selected and recorded before implementation.

## 4. Architecture approach

Start with a modular Spring Boot backend with explicit domain boundaries. Do not require every domain to run as an independent service in the first release.

Potential domains:

1. **Identity and Accounts:** account status, authentication credentials, verification state, and sessions/tokens.
2. **Profiles:** username, display name, biography, profile image reference, and privacy preference.
3. **Social Graph:** follow relationships, follow requests, and blocks.
4. **Notes and Media:** note content, author, media references, edit/delete rules, and upload lifecycle.
5. **Engagement and Moderation:** likes, comments, reports, and moderation records.
6. **Feed:** retrieval of eligible notes with documented ordering and pagination.

Each domain controls its business rules and write operations. Other domains must not bypass ownership boundaries by directly modifying its tables. In the initial modular backend, modules may share one database, but code-level ownership rules and service interfaces must remain explicit.

Service extraction will be based on deployment independence, scaling, fault isolation, operational cost, and justified learning objectives—not simply because a domain has a name.

## 5. Social and privacy model

- Public accounts can be followed immediately, unless a block or another platform rule prevents it.
- Private accounts require approval before a follow relationship becomes active.
- Pending requests do not grant access to protected content.
- Unfollowing removes access that depends on the relationship.
- Blocking takes precedence over ordinary follow relationships.
- Users may edit or delete only their own notes, apart from a separately authorized moderation process.
- All protected-content requests must be authorized by the backend.
- Media access must follow the associated content's access policy. Knowing or guessing an object key must not grant access to private media.
- Visibility checks must be applied consistently to note detail, feeds, profiles, comments, likes, search (if added), and media retrieval.

The MVP proposes that note visibility follows the author's account privacy and the viewer's relationship to that author. Per-note audience controls are deferred unless validated product needs require them. Whether public content is available to anonymous visitors remains open.

## 6. Authentication

Initial registration uses a phone number, password, and OTP verification. Password login issues a short-lived access token and a refresh credential according to a defined refresh strategy.

The design must include:

- Appropriate one-way password hashing.
- OTP expiration, attempt limits, rate limiting, and replay prevention.
- Generic responses where needed to reduce phone-number enumeration.
- Short-lived access tokens and token validation.
- Refresh-token rotation and revocation, including a defined response to suspected token reuse.
- Secure Android storage for credentials/tokens.
- Secret and signing-key management outside source control.
- Phone-number change, account recovery, logout, and compromised-account procedures.

The SMS provider, token lifetimes, refresh-token format/storage, and detailed recovery policies remain open decisions. See ADR 0008.

## 7. Data and media

MySQL stores structured account, profile, relationship, note metadata, engagement, and moderation records. Drawing images and other media are stored in object storage. The database stores opaque object keys and related metadata rather than large binary files.

Schema changes will be version-controlled using migrations. Database constraints and indexes will enforce important invariants such as unique normalized usernames, unique follow pairs, and no duplicate likes. Business rules must also be enforced at the application layer; database constraints alone do not replace authorization.

Object storage access must be designed so private media cannot be retrieved simply by guessing or retaining a permanent public URL. A controlled upload flow and short-lived, scoped access URLs are likely candidates, subject to final design.

## 8. API conventions

- Base path: `/api/v1`.
- JSON request and response formats.
- Resource-oriented paths and appropriate HTTP methods.
- Request/response DTOs rather than exposed persistence entities.
- Consistent error response structure.
- Bean Validation and centralized exception handling.
- Authentication and authorization on protected operations.
- Pagination for feed and collection endpoints.
- OpenAPI documentation.
- Stable API contracts and deliberate version changes.

See [`api-conventions.md`](api-conventions.md) for the draft API surface and response rules.

## 9. Quality and operations

The development process will include unit tests, integration tests, API-level tests, automated builds, code review, containerization, health checks, structured logging, and environment-specific configuration.

Configuration must be separated by environment. Secrets must not be committed. Production launch planning must include deployment, monitoring, backup/restore, rollback, incident response, cost alerts, and data-retention decisions.

Before public launch, the project must undergo security, privacy, moderation, age-related compliance, operational recovery, and cost reviews.

## 10. Open decisions

The following must be resolved or explicitly deferred before their associated features are implemented:

- SMS provider and OTP delivery policy.
- Token lifetime, refresh-token storage, and recovery flows.
- Exact username and phone-number normalization rules.
- Note length, image dimensions, upload limits, and allowed formats.
- Whether notes can have individual audience settings.
- Anonymous viewing policy for public content.
- Moderation workflow and retention requirements.
- Backend service extraction criteria and measurable triggers.
- AWS deployment runtime, region, security configuration, and budget.
- Applicable privacy, age, and child-safety requirements for the intended audience.
- Availability, latency, scale, backup, and recovery objectives.

## 11. Change management

Significant architectural decisions will be recorded in numbered Architecture Decision Records under `docs/adr/`.

An ADR must explain the context, decision, alternatives considered where relevant, consequences, status, and conditions that may cause the decision to be revisited.

This baseline is not a declaration of production readiness. It is the reviewed starting point for incremental development and will evolve as requirements are validated.
