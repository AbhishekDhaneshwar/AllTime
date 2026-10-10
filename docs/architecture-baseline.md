# AllTime — Architecture Baseline

## 1. Purpose

This document defines the initial technical architecture and architectural boundaries for AllTime.

AllTime is a social platform primarily intended for users in India aged 16–35, with potential expansion to users up to age 40. Its signature feature allows users to create notes, draw or doodle on them, and share them with their communities.

This baseline establishes the initial architecture before application implementation begins. Detailed implementation decisions may be documented in subsequent Architecture Decision Records (ADRs).

## 2. Architectural Approach

AllTime will use a **microservices architecture from day one**.

Each service will represent a distinct business capability, own its data, expose well-defined interfaces, and be independently buildable and deployable.

The initial architecture will prioritize clear service boundaries and a manageable operational footprint. Additional services and infrastructure components will be introduced only when justified by product or operational requirements.

## 3. Client and Technology Stack

### Android Application

* Native Android application.
* Java as the initial application programming language.
* Android Studio as the primary development environment.
* Communication with backend services through HTTPS-based APIs.

### Backend

* Java 21.
* Spring Boot.
* REST APIs using JSON.
* Maven Wrapper for reproducible builds.
* Spring Security for authentication and authorization.
* JWT-style access tokens and refresh tokens.

### Data and Storage

* MySQL for structured application data.
* Separate logical databases for independently owned service data.
* Amazon S3 or an equivalent object-storage service for drawings, uploaded media, and other binary assets.
* Services must not directly access another service's database tables.

### Deployment

* AWS as the planned cloud deployment platform.
* Docker-compatible container images for backend services.
* CI/CD, centralized logging, metrics, tracing, and health monitoring introduced incrementally.

## 4. Initial Microservices

The initial backend will contain the following five business services and an API Gateway.

### 4.1 Identity Service

Responsibilities:

* Phone-number-based registration and login.
* OTP verification.
* Password hashing and credential management.
* Access-token issuance and refresh-token lifecycle management.
* Authentication-related account security operations.

Data ownership:

* Authentication credentials.
* Phone-number verification state.
* Refresh-token or session records.
* Authentication-related security records.

OTP delivery will use an appropriate external SMS provider. OTPs must have an expiry period, be rate-limited, and be protected against replay and brute-force attempts.

### 4.2 Social Service

Responsibilities:

* User profiles and profile updates.
* Public and private account settings.
* Following and unfollowing users.
* Follow requests and approval or rejection.
* Blocking and related relationship restrictions.

Data ownership:

* User profile information.
* Account visibility settings.
* Follow relationships and follow-request state.
* Block relationships.

Authentication credentials remain exclusively owned by the Identity Service.

### 4.3 Content Service

Responsibilities:

* Creating, editing, retrieving, and deleting notes and posts.
* Supporting text-based notes and drawing/doodle content.
* Managing post visibility and content metadata.
* Managing media upload references and lifecycle.
* Enforcing content ownership and visibility rules.

Data ownership:

* Posts and notes.
* Content metadata and ownership references.
* Media object references and content visibility state.

Binary drawing and media files will be stored in object storage rather than directly in MySQL. Upload and download access must be controlled, and private media must not be publicly accessible by default.

### 4.4 Engagement Service

Responsibilities:

* Liking and unliking posts.
* Creating, retrieving, and deleting comments.
* Enforcing applicable interaction restrictions.

Data ownership:

* Likes.
* Comments.
* Engagement-related records.

The service will use stable post identifiers to reference content owned by the Content Service. It will not directly query the Content Service database.

### 4.5 Feed Service

Responsibilities:

* Providing the home feed.
* Selecting eligible posts based on social relationships and content visibility.
* Supporting pagination and feed retrieval.
* Evolving feed ordering and ranking as product requirements develop.

Data ownership:

* Feed-specific state, if required.
* Feed ranking or materialized feed entries, if introduced.

The Feed Service will not become the authoritative owner of posts, user profiles, or follow relationships. It may maintain derived data for performance, provided that its source and synchronization strategy are defined.

### 4.6 API Gateway

Responsibilities:

* Providing a common backend entry point for the Android application.
* Routing requests to the appropriate service.
* Applying appropriate shared edge concerns, such as request limits and correlation identifiers.
* Supporting centralized entry-point security policies where appropriate.

The gateway will not own business data or replace authorization checks inside individual services.

## 5. Data Ownership and Database Boundaries

Each business service owns its data and is the sole authority for modifying that data.

Initial logical database boundaries:

* Identity Service — identity database.
* Social Service — social database.
* Content Service — content database.
* Engagement Service — engagement database.
* Feed Service — feed database, introduced when needed.

During early development, these databases may run on a shared MySQL instance to reduce infrastructure costs and operational complexity. Logical separation must be maintained, including service-specific database credentials and permissions.

Each service must use its own schema migrations and must not modify another service's schema.

Cross-service data access will use service APIs or asynchronous events. Database joins across service boundaries are not permitted.

## 6. Inter-Service Communication

REST over HTTPS will be the initial mechanism for synchronous service-to-service communication.

Asynchronous messaging may be introduced for use cases such as feed updates, notifications, and other background processing when the need is established.

Message brokers, service discovery, distributed tracing, and advanced resilience mechanisms will be introduced incrementally rather than all at once.

Service integrations must account for timeouts, failures, retries where safe, and duplicate message processing where asynchronous messaging is used.

## 7. Authentication and Authorization

The Identity Service will authenticate users and issue access tokens with appropriate expiry and claims. Refresh tokens will be handled securely and support revocation or rotation according to the authentication design.

Backend services must validate access tokens and enforce authorization for their own resources.

Authorization must account for:

* Authenticated user identity.
* Resource ownership.
* Public and private account visibility.
* Follow-request approval where required.
* Blocking restrictions.
* Post visibility and interaction permissions.

The API Gateway alone is not a sufficient security boundary.

Passwords and OTPs must never be logged in plaintext. Secrets and signing keys must not be committed to Git.

## 8. Content and Media Storage

MySQL will store structured data and references to media objects.

Object storage will hold drawing files, images, and other binary media. The Content Service will control media metadata, ownership, and access policy.

The implementation must consider upload validation, file-size limits, content-type validation, controlled access, and cleanup of abandoned uploads or deleted content.

## 9. Reliability and Observability

Each service must support health checks and expose sufficient operational information for troubleshooting.

The architecture will progressively introduce:

* Application logs with correlation identifiers.
* Health, readiness, and liveness checks where supported.
* Metrics and distributed tracing.
* Timeouts and carefully bounded retries.
* Resilience patterns for appropriate remote calls.
* Centralized monitoring and alerting.

Failures in one service should be handled without assuming that every other service is always available.

## 10. Testing Strategy

The implementation will include:

* Unit tests for business logic.
* Integration tests for database and framework interactions.
* API tests for service contracts.
* Security tests for authentication and authorization.
* Integration tests for cross-service workflows.
* End-to-end tests for critical user journeys as the application matures.

Service boundaries and data ownership should be verifiable through tests and configuration.

## 11. Deployment and Evolution

Each service must be independently buildable and deployable.

The initial deployment can use a small number of AWS resources and a straightforward CI/CD pipeline. Infrastructure complexity should increase only when operational needs justify it.

Service discovery, centralized configuration, messaging, caching, orchestration, and other infrastructure components will be added according to explicit requirements and documented decisions.

Kubernetes is not a prerequisite for the initial implementation.

Database schema changes must be backward-compatible where necessary to support independently deployed service versions.

## 12. Initial Implementation Order

The backend foundation will be developed incrementally:

1. Establish the repository structure and shared engineering conventions.
2. Create the Identity Service foundation.
3. Establish database migrations, configuration, validation, error handling, and automated tests.
4. Implement authentication and token lifecycle management.
5. Establish the Social Service and its relationship with authenticated identities.
6. Implement the Content Service and controlled media storage.
7. Add the Engagement Service.
8. Add the Feed Service and required cross-service integration.
9. Introduce gateway routing and additional infrastructure in a controlled sequence.

This order may be adjusted when a technical dependency requires it, without changing the agreed microservices architecture.

## 13. Architecture Governance

Architecture decisions that materially affect service boundaries, security, persistence, or communication must be recorded in ADRs.

The architecture baseline and its ADRs must reflect the selected microservices approach before application coding begins.

This document establishes the initial direction, not a requirement to implement every planned infrastructure component immediately.
