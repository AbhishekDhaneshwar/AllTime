# AllTime — API Conventions

**Status:** Proposed — draft contract  
**Version:** 0.1  
**Last updated:** 10 October 2026

This document defines the initial conventions for the Android client and Spring Boot backend. Endpoint names and payloads are a draft to review before implementation; they are not yet a stable public API.

## 1. Base conventions

- Base path: `/api/v1`.
- Transport: HTTPS outside local development.
- Content type: `application/json` for JSON request/response bodies.
- Character encoding: UTF-8.
- Resource names: plural nouns where appropriate; use HTTP methods to express operations.
- Authentication: protected requests use `Authorization: Bearer <access-token>`.
- Use request/response DTOs; do not expose JPA entities or internal persistence structure.
- The server derives the acting user from the authenticated principal. A client-supplied `authorId` or `actingUserId` must not determine ownership.
- Validate input at the API boundary and enforce business rules and authorization in the service/domain layer.
- Document the contract using OpenAPI once the backend is created.

## 2. Draft endpoint catalogue

### Authentication

| Method | Path | Purpose | Notes |
|---|---|---|---|
| `POST` | `/api/v1/auth/registration-otp` | Request a registration OTP | Rate-limited; response should not unnecessarily reveal account state. |
| `POST` | `/api/v1/auth/register` | Register using phone, password, and the valid OTP challenge/code | Account becomes verified only after successful verification. Exact payload sequencing is to be finalized. |
| `POST` | `/api/v1/auth/login` | Sign in using phone and password | Returns an access token and refresh credential according to the auth contract. |
| `POST` | `/api/v1/auth/refresh` | Refresh authentication | Rotation/reuse detection rules must be defined. |
| `POST` | `/api/v1/auth/logout` | Revoke refresh credential/session | Exact session semantics are to be defined. |

### Profiles and social graph

| Method | Path | Purpose | Expected behavior |
|---|---|---|---|
| `GET` | `/api/v1/users/{userId}` | Read a profile | Return only information visible to the requester. |
| `PATCH` | `/api/v1/users/me` | Update the current user's profile | Allow-list editable fields; validate username and profile values. |
| `POST` | `/api/v1/users/{userId}/follow` | Follow or request to follow | Public account: active follow; private account: pending request. Block rules take precedence. |
| `DELETE` | `/api/v1/users/{userId}/follow` | Unfollow/cancel follow | Remove the relationship-dependent access. |
| `GET` | `/api/v1/users/me/follow-requests` | List requests received by the current user | Owner-only; paginated. |
| `POST` | `/api/v1/users/me/follow-requests/{requestId}/accept` | Accept a follow request | Only the target account owner can accept. |
| `POST` | `/api/v1/users/me/follow-requests/{requestId}/reject` | Reject a follow request | Only the target account owner can reject. |
| `PUT` | `/api/v1/users/{userId}/block` | Block an account | Define behavior for existing follows and interactions. |
| `DELETE` | `/api/v1/users/{userId}/block` | Unblock an account | Does not automatically recreate a previous follow. |

### Notes, feed, and engagement

| Method | Path | Purpose | Expected behavior |
|---|---|---|---|
| `POST` | `/api/v1/notes` | Create a note | Author derived from authentication; accepts text and/or a media reference according to the final schema. |
| `GET` | `/api/v1/notes/{noteId}` | Read a note | Apply visibility, follow, and block rules. |
| `PATCH` | `/api/v1/notes/{noteId}` | Edit a note | Author-only; validate changed fields. |
| `DELETE` | `/api/v1/notes/{noteId}` | Delete a note | Author-only; define media cleanup and deletion semantics. |
| `GET` | `/api/v1/feed?cursor={cursor}` | Read a feed page | Return only authorized notes; cursor format is opaque to clients. |
| `PUT` | `/api/v1/notes/{noteId}/like` | Like a note | Idempotent target state; enforce visibility and block rules. |
| `DELETE` | `/api/v1/notes/{noteId}/like` | Unlike a note | Idempotent target state. |
| `POST` | `/api/v1/notes/{noteId}/comments` | Add a comment | Enforce visibility, validation, and block rules. |
| `GET` | `/api/v1/notes/{noteId}/comments` | List comments | Paginated and visibility-aware. |
| `POST` | `/api/v1/reports` | Report an account or content item | Validate target and reason; do not expose moderation internals. |

A dedicated media-upload initiation/completion flow is expected if drawings are uploaded directly to object storage. Its exact endpoints are intentionally not fixed until storage and authorization details are designed.

## 3. Request and response rules

- Use `camelCase` JSON property names.
- Timestamps should be UTC in ISO 8601/RFC 3339 form, for example `2026-10-10T12:30:00Z`.
- IDs are opaque to clients. The exact ID format is an implementation decision and should not leak database internals unnecessarily.
- Do not accept ownership fields that can be derived from authentication, such as `authorId`, for ordinary note creation.
- Ignore no invalid field silently. Reject unsupported or malformed values with a clear, non-sensitive error.
- Avoid returning password hashes, OTP details, raw refresh tokens except at the required issuance point, internal object-store credentials, or private moderation notes.

### Illustrative create-note request

```http
POST /api/v1/notes
Authorization: Bearer <access-token>
Content-Type: application/json
```

```json
{
  "text": "A little progress every day.",
  "contentType": "TEXT"
}
```

This example is illustrative, not a finalized schema. A successful creation would normally return `201 Created` with a response body containing the new note's ID, author ID, accepted content fields, and creation timestamp. The server determines the author from the authenticated principal.

## 4. HTTP status conventions

| Status | Use |
|---|---|
| `200 OK` | Successful read or update with a response body. |
| `201 Created` | A resource has been created. Include a resource identifier and, where appropriate, a `Location` header. |
| `204 No Content` | Successful operation with no response body, commonly delete/unfollow/unlike. |
| `400 Bad Request` | Malformed request or validation failure. |
| `401 Unauthorized` | Missing, invalid, expired, or otherwise unacceptable authentication. |
| `403 Forbidden` | Authenticated request is not permitted, where disclosure of resource existence is acceptable. |
| `404 Not Found` | Resource is absent or deliberately concealed to avoid revealing protected-resource existence. |
| `409 Conflict` | State conflict such as a username uniqueness violation or incompatible relationship state. |
| `429 Too Many Requests` | Rate limit exceeded; include retry guidance where safe and appropriate. |
| `500 Internal Server Error` | Unexpected server failure; do not return stack traces or secrets. |

The precise response for a private resource must avoid leaking whether that resource exists when that detail itself is sensitive.

## 5. Error response shape

Use one consistent error envelope. A Problem Details-style response is a candidate; the exact implementation can be standardized when the backend exception layer is built.

Illustrative example:

```json
{
  "type": "https://alltime.example/problems/validation-error",
  "title": "Request validation failed",
  "status": 400,
  "detail": "One or more fields are invalid.",
  "instance": "/api/v1/users/me",
  "fieldErrors": [
    {
      "field": "username",
      "code": "Size",
      "message": "Username length is outside the allowed range."
    }
  ],
  "traceId": "example-trace-id"
}
```

The example domain is a placeholder and must not be used as a real production URL without being deliberately established. Error messages should be safe and actionable but must not disclose sensitive account, token, or system details.

## 6. Pagination

- Feed pagination should use an opaque cursor rather than a page number, because feeds can change while the client is scrolling.
- The API should return an explicit next-cursor indicator, for example `nextCursor` and `hasMore`.
- Collection endpoints should impose a server-side maximum page size. Clients may request a smaller page size.
- Use a stable ordering with a deterministic tie-breaker so entries do not unpredictably repeat or disappear between pages.
- Apply visibility and block checks before including an item in a returned page.

An illustrative response shape is:

```json
{
  "items": [],
  "nextCursor": null,
  "hasMore": false
}
```

The exact cursor encoding and page-size maximum are open implementation decisions.

## 7. Authentication and authorization rules

- Public endpoints must be intentionally identified; everything else should be protected by default where practical.
- Authentication proves the caller's identity; authorization decides whether the caller can perform the requested action.
- Never rely on the Android client to enforce private-account or ownership checks.
- Apply the same content-visibility decision to feed, note detail, comments, media, and any future search endpoint.
- Use rate limits and anti-abuse controls for registration, OTP, login, refresh, comments, and reports as appropriate.
- Logs must not contain passwords, OTP values, access/refresh tokens, or private content bodies.

## 8. API change policy

A compatible additive change may be made within `/api/v1` if existing clients continue to work. Removing/renaming fields, changing meanings, or changing authorization behavior requires deliberate compatibility review. Update this document, OpenAPI definitions, and relevant tests when contracts change.
