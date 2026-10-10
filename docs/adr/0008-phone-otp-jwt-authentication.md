# ADR 0008: Use phone OTP verification and password login with access and refresh tokens

- **Status:** Proposed
- **Date:** 10 October 2026
- **Decision owners:** AllTime project maintainers

## Context

AllTime's initial sign-up requirement is phone-number verification paired with password-based login. The Android client needs an authentication mechanism for REST APIs, while the backend must support expired access tokens, logout, device/session management, and revocation after suspected compromise.

## Decision

Use phone number plus OTP verification during registration, and phone number plus password for subsequent login. Use short-lived JWT-style access tokens for authenticated API requests together with a separately managed refresh-token strategy. Refresh credentials must support rotation and revocation. The detailed OTP workflow, token lifetimes, refresh-token format/storage, SMS provider, and recovery policy remain open decisions; do not invent these values independently in the client and server.

## Security requirements

- Hash passwords with a suitable, maintained password-hashing algorithm and safe parameters.
- Do not log or persist OTP values in plaintext application logs.
- Enforce OTP expiry, single-use/replay prevention, attempt limits, rate limits, and abuse monitoring.
- Avoid overly informative responses that allow attackers to enumerate registered phone numbers.
- Validate access-token signature, issuer/audience where applicable, expiry, and relevant claims.
- Keep access tokens short-lived; choose the precise lifetime based on threat model and usability.
- Rotate refresh tokens on use and revoke the active token family/session on detected reuse according to a documented policy.
- Decide whether refresh credentials are opaque random values or signed tokens, and store them so server-side revocation and reuse detection are practical.
- Use Android's appropriate secure storage mechanisms; never place production tokens in source control, logs, analytics, or insecure persistent preferences.
- Keep signing keys, SMS credentials, and other secrets outside the repository; plan key rotation.
- Define logout, all-device logout, phone-number change, account recovery, and compromised-account procedures.

## Alternatives considered

- **Phone number only with OTP on every login:** can reduce password handling but increases SMS dependency and recurring cost, and SMS has known account-takeover risks.
- **Email-and-password authentication:** may be appropriate for some products but does not match the chosen phone-number-first onboarding direction.
- **Long-lived access tokens without refresh/revocation:** simpler, but increases impact when a token is stolen.
- **Server-side opaque session only:** can simplify revocation but requires session lookup on authenticated requests and does not match the current JWT-style access-token direction.
- **Third-party identity provider:** may reduce some custom security work but introduces provider dependency, cost, and a different user flow.

## Consequences

### Positive

- Phone verification provides evidence that the user can receive messages at the supplied number at registration time.
- Access tokens allow API authentication without a database lookup for every request if the chosen validation design permits it.
- Refresh rotation and revocation provide a path to session control and mitigation of token theft.

### Negative / trade-offs

- SMS delivery can be delayed, unavailable, expensive, or abused; provider and country-specific deliverability need testing.
- Phone numbers can change or be recycled, so recovery and number-change flows need careful design.
- Token rotation, multi-device sessions, reuse detection, and secure mobile storage require tests and documentation.
- A JWT access token is not automatically revocable before expiry; keep it short-lived and decide whether additional revocation controls are needed.

## Open decisions before implementation

- SMS provider and delivery/fallback strategy.
- OTP length, expiry, retry limit, resend cooldown, and rate-limit dimensions.
- Registration challenge lifecycle and how the OTP is bound to the phone-number challenge.
- Access-token lifetime, signing algorithm/key management, issuer/audience, and required claims.
- Refresh-token format, storage, rotation, revocation, reuse handling, and maximum session age.
- Multi-device/session display and “log out all devices” behavior.
- Phone-number change and account-recovery verification.
- Minimum permitted age and any additional age-related onboarding requirements.

## Revisit when

Revisit if product research, threat modelling, regulatory requirements, or provider constraints favor an identity provider or a different primary authentication mechanism. Security changes require migration planning for active sessions.
