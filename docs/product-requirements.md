# AllTime — Product Requirements

**Status:** Proposed — pending review  
**Version:** 0.1  
**Last updated:** 10 October 2026

## 1. Purpose

AllTime is a social platform for users in India, primarily expected to be aged 16–35, with possible relevance up to age 40. Its defining experience is sharing written notes and drawings/doodles with a community of friends and followers.

This document sets the first product boundary for the MVP. It is a working baseline, not a promise that every listed feature will be delivered in the first build.

## 2. Product goals

1. Make it straightforward to create an account and establish a recognizable profile.
2. Let people share text and drawing-based notes with an audience controlled by account privacy and social relationships.
3. Make following and private-account approval understandable and predictable.
4. Provide a useful feed without requiring a complex recommendation system.
5. Establish privacy, security, moderation, and maintainability foundations from the start.

## 3. Target users and roles

- **Registered user:** Can manage their account and profile, create notes, follow permitted accounts, and engage with content.
- **Public account:** Allows eligible users to follow without a manual approval step, subject to blocking and platform rules.
- **Private account:** Requires the account owner to approve follow requests before the requester receives follower-based access.
- **Content author:** Owns the notes they create and can edit or delete their own notes.
- **Moderator/administrator:** A future operational role for reviewing reports and enforcing platform rules. Its permissions and tooling must be specified before launch.

The intended age range includes possible users under 18. The minimum permitted age, age assurance, consent requirements, and additional protections remain explicit launch decisions and require appropriate legal/privacy review.

## 4. MVP scope

### Included in the planned MVP

- Phone-number registration with OTP verification.
- Password-based login and token-based authenticated API access.
- Account/profile retrieval and editing.
- Public/private profile setting.
- Follow, unfollow, private-account follow requests, request acceptance/rejection, and blocking.
- Creating text notes and notes with drawing/media content.
- Retrieving a note and editing/deleting one's own notes.
- A paginated home feed.
- Likes and comments.
- Basic reporting of accounts or content.
- Backend validation, authorization, automated tests, health checks, and useful logs.

### Explicitly deferred

- Direct messages and group chats.
- Stories or ephemeral posts.
- Live audio/video and video streaming.
- Advanced personalized recommendations or ranking models.
- Advertising, creator payments, subscriptions, and monetization.
- Complex per-note audience lists, unless user research shows they are necessary.
- A full moderation operations console.

Deferral means “not part of the initial MVP,” not necessarily “never.”

## 5. Core user journeys

### 5.1 Register and sign in

1. A user submits a phone number to request a registration OTP.
2. The system applies rate and attempt limits, issues a time-limited challenge, and sends the OTP through a configured provider.
3. The user submits the OTP and a password as part of registration.
4. The server validates the challenge and creates a verified account only once.
5. The user signs in with their phone number and password and receives an access token plus a refresh credential according to the authentication design.
6. When the access token expires, the client uses the refresh flow; logout/revocation invalidates the relevant refresh credential or session family.

Exact request sequencing, OTP lifetime, token lifetime, and provider are implementation decisions to be recorded before the authentication feature is built.

### 5.2 Set up a profile

A registered user chooses a unique username and can update display name, biography, profile image, and public/private preference. The server validates all values and does not allow one user to overwrite another user's profile.

### 5.3 Follow a public account

A user follows a public account. If neither account blocks the other and platform rules allow the action, the relationship becomes active without manual approval.

### 5.4 Request to follow a private account

A requester sends a follow request. Until the account owner accepts it, the requester does not receive follower-based access to protected content. The owner may accept or reject the request.

### 5.5 Create and share a note

An authenticated user creates a note containing text, drawing/media, or both. The server derives the author identity from the authenticated principal, validates the request, stores metadata, and applies the account's visibility policy when other users retrieve it.

### 5.6 View the feed

An authenticated user requests a page of notes. The server returns only content the viewer is authorized to access and uses a stable pagination contract. The initial ordering policy should be simple and documented; advanced personalized ranking is deferred.

### 5.7 Block a user

A user blocks another account. Blocking overrides ordinary follow relationships and prevents the blocked relationship from being used to grant access or interactions. Exact effects on historical comments, likes, search, and existing content must be specified consistently before release.

### 5.8 Report content

A user submits a report with a target and reason. The system validates and records the report without promising immediate moderation action. Report review, escalation, retention, and response times must be defined before public launch.

## 6. Functional requirements

Priority labels: **P0** = required for a usable MVP foundation; **P1** = planned MVP functionality but can follow core account and note flows.

| ID | Priority | Requirement |
|---|---|---|
| FR-001 | P0 | Registration must verify control of the supplied phone number before activating the account. |
| FR-002 | P0 | Passwords must be stored using an appropriate password-hashing algorithm, never as plaintext or reversible application-encrypted text. |
| FR-003 | P0 | Authenticated APIs must derive the acting user from verified server-side authentication, not a client-supplied author/user ID. |
| FR-004 | P0 | Users can retrieve and update only profile fields they are permitted to manage. |
| FR-005 | P0 | The system supports public and private account settings. |
| FR-006 | P0 | Public accounts can be followed without manual approval unless a block or other rule prevents it. |
| FR-007 | P0 | Private accounts require an accepted follow request before follower-based content access is granted. |
| FR-008 | P0 | Blocking overrides normal follow access and interaction rules. |
| FR-009 | P0 | A user can create a text note and/or a drawing/media-backed note. |
| FR-010 | P0 | Only the note's author or an explicitly authorized administrative process can edit/delete that note. |
| FR-011 | P0 | Note and media retrieval must enforce visibility and relationship rules on the server. |
| FR-012 | P0 | Feed and list APIs must be paginated and must not return protected content to unauthorized viewers. |
| FR-013 | P1 | Users can like and unlike notes without creating duplicate likes. |
| FR-014 | P1 | Users can comment on notes subject to visibility, blocking, validation, and moderation rules. |
| FR-015 | P1 | Users can submit reports for eligible accounts or content. |
| FR-016 | P0 | Authentication and OTP endpoints must enforce rate limits and sensible failure responses. |
| FR-017 | P0 | The system must provide consistent API errors and request validation. |
| FR-018 | P0 | Schema changes must be versioned and reproducible in development/test environments. |

## 7. Non-functional requirements

- **Security:** Enforce authorization server-side; hash passwords; protect tokens and secrets; validate input; limit OTP and authentication abuse; prevent insecure direct-object access.
- **Privacy:** Minimize collected personal data; limit access to private-account content; ensure media storage follows the same access policy as its parent content.
- **Reliability:** Handle expected failures with consistent errors and logs; document backup/restore and recovery before launch.
- **Maintainability:** Keep domain boundaries explicit, separate API DTOs from persistence entities, and use meaningful automated tests.
- **Performance:** Paginate feed and collection reads, create indexes for known access patterns, and measure rather than prematurely optimize.
- **Observability:** Provide health endpoints, structured application logs, and enough diagnostic context to investigate failures without logging secrets or private content.
- **Portability:** Provide a repeatable local build using the Maven Wrapper and environment-based configuration.
- **Accessibility and usability:** The Android experience should use clear validation and error messages and should be tested on representative device sizes and network conditions.
- **Cost awareness:** Define budgets, logging retention, media limits, and AWS resource safeguards before hosting a public service.

No numeric availability, latency, scale, or recovery-time target has been approved yet. Those targets should be set before production launch.

## 8. Initial acceptance criteria

The first end-to-end slice is acceptable when:

- A fresh clone can build and run using documented steps.
- Registration/login paths are tested, including invalid OTP, expired OTP, repeated attempts, and incorrect password.
- A verified user can create and retrieve their own note.
- Authorization tests prove that one user cannot edit/delete another user's note.
- Tests cover public follow and pending/accepted private follow behavior.
- Blocking and private-account rules are covered by tests for every content retrieval path implemented so far.
- Database migrations work against a fresh test database.
- No secrets are required to be committed to the repository.
- The CI pipeline runs build and tests on proposed changes once CI is configured.

These criteria will be expanded per feature as implementation proceeds.

## 9. Open product questions

- What is the minimum eligible age, and what age-assurance and consent steps are required?
- Can public content be viewed without signing in, or must all content access be authenticated?
- Are notes ever independently private/public, or is visibility controlled only by account privacy in the MVP?
- Can users edit a note after publication, and should edit history be retained?
- Which drawing formats and upload sizes are permitted? Will drawing happen in the Android client and be uploaded as an image, or will a structured stroke format be stored?
- What are the username rules, reserved names, and account deletion behavior?
- What exact effects should blocking have on search, comments, likes, and existing interactions?
- What reporting categories, review workflow, appeal path, and data-retention rules are needed?
- What baseline traffic, latency, availability, backup, and recovery targets should guide AWS design?

## 10. Change process

When product scope or behavior changes, update this document and the affected architecture/API documents in the same change where possible. Record substantial architectural trade-offs as ADRs under `docs/adr/`.
