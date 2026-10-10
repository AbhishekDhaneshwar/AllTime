# ADR 0004: Use following relationships and optional private accounts for content access

- **Status:** Proposed
- **Date:** 10 October 2026
- **Decision owners:** AllTime project maintainers

## Context

AllTime needs a familiar social connection model that supports open profiles and profiles where the owner controls who becomes a follower. Follow status affects note visibility and interactions, so the policy must be consistent across API paths.

## Decision

Adopt a following model with public and private accounts:

- A follow action on a public account creates an active relationship, subject to blocking and platform rules.
- A follow action on a private account creates a pending request.
- Pending requests grant no follower-based access; acceptance activates the relationship.
- Unfollowing/cancelling removes the relevant relationship or request.
- Blocking overrides ordinary follow relationships and prevents relationship-based access or interactions between the accounts.
- The backend enforces these decisions for every protected-content path.
- In the initial MVP, note visibility follows account privacy and the viewer's relationship to the author; per-note audience controls are deferred.

The anonymous-viewing policy for public accounts and the exact handling of historical interactions when blocking are open product decisions that must be specified before release.

## Alternatives considered

- **Public profiles only:** simpler to implement but does not meet the private-account requirement.
- **Per-note audiences in the first release:** more expressive but adds complex policy checks to every feed, media, search, and engagement path.
- **Friendship requiring mutual acceptance:** changes the social model and adds reciprocal workflow not currently required.

## Consequences

### Positive

- The model is understandable and maps to well-known social-network behavior.
- One shared authorization policy can be tested and reused across content surfaces.
- Deferring per-note visibility limits the number of access states in the MVP.

### Negative / trade-offs

- Relationship checks add work to feed and content retrieval, and query/index design must support them.
- Any missed authorization path can disclose private content; security tests are mandatory.
- Unfollow/block behavior for existing comments, likes, caches, and media requires consistent rules.

## Implementation requirements

- Enforce unique follower/followed pairs and prohibit self-following.
- Ensure pending status never satisfies the authorization check for an accepted follower.
- Test combinations of public/private, pending/accepted, followed/unfollowed, blocked/unblocked, and author/viewer.
- Apply visibility rules to media URLs as well as API responses.

## Revisit when

Revisit if product research requires close-friends lists, per-note audiences, public anonymous access, or a different relationship model. Treat any change as a security-sensitive policy migration.
