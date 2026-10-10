# ADR 0006: Store drawings and uploaded media in object storage

- **Status:** Proposed
- **Date:** 10 October 2026
- **Decision owners:** AllTime project maintainers

## Context

The signature AllTime experience includes notes with drawings or doodles. Media may be larger than ordinary structured database fields and can have different storage, delivery, and lifecycle needs from account and note metadata.

## Decision

Store media payloads in object storage and keep opaque object keys, content type, size, ownership, and lifecycle metadata in MySQL. Design upload and read flows so the backend authorizes the operation and private media cannot be accessed merely by guessing a key or reusing an unrestricted permanent URL. The specific AWS storage service, upload protocol, image processing, and access URL strategy remain to be selected during implementation.

## Alternatives considered

- **Store image bytes directly in MySQL:** keeps data together but increases database size and backup/restore pressure for the expected media pattern.
- **Store files on the application server's local disk:** easy for a single local instance, but fragile across container replacement, scaling, and deployments.
- **Use permanently public media URLs:** simple delivery, but incompatible with private-account access expectations.
- **Upload every byte through the backend:** provides centralized control but may create unnecessary bandwidth and compute load; a controlled direct-to-object-storage flow may be preferable.

## Consequences

### Positive

- Separates structured relational data from larger media payloads.
- Supports scalable media delivery and lifecycle policies.
- Database rows remain relatively small and can refer to media by key.

### Negative / trade-offs

- Database and object storage are separate systems; cleanup, retries, and consistency need explicit handling.
- Upload authorization and short-lived access URLs add complexity.
- Media processing, file validation, malware/content scanning where appropriate, and cost controls need planning.

## Implementation requirements

- Validate allowed media types and size limits on the server or storage workflow; never trust client-supplied MIME type alone.
- Use generated, opaque object keys instead of user-controlled paths.
- Enforce authorization before issuing read access to private media.
- Define cleanup for failed uploads, abandoned multipart uploads, deleted notes, and account deletion.
- Avoid exposing storage credentials to the Android client.

## Revisit when

Revisit if the product moves to structured drawing strokes, collaborative canvas data, or other media formats that change storage and rendering requirements.
