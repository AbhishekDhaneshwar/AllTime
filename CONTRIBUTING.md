# Contributing to AllTime

Thank you for helping build AllTime. The project is being developed incrementally, with documentation and architecture decisions reviewed before implementation.

## Working principles

- Prefer simple, maintainable solutions over premature complexity.
- Preserve the domain boundaries described in `docs/architecture-baseline.md`.
- Treat privacy, authorization, input validation, and abuse prevention as core requirements, not follow-up polish.
- Add tests for new behavior and regression fixes.
- Never commit credentials, OTPs, access tokens, private keys, production data, or real user data.
- Document meaningful changes to architecture, APIs, data ownership, or security in an ADR.

## Branch workflow

Use a short-lived branch for each change. Suggested branch names:

```text
feature/profile-api
feature/note-creation
fix/follow-authorization
docs/update-api-conventions
chore/add-ci-build
```

Do not develop directly on the default branch once the repository workflow is established. Keep branches focused and sync with the current default branch before opening a pull request or merging.

## Commit messages

Use clear, action-oriented messages. A Conventional Commits-style prefix is recommended:

```text
docs: add initial architecture baseline
feat: add account registration endpoint
fix: reject follow request from blocked user
test: cover private-profile authorization
chore: configure backend build
```

The prefix is a project convention, not a guarantee of automated release behavior.

## Before opening a pull request

- Explain what changed and why.
- Link the relevant requirement, issue, or ADR when applicable.
- Run the relevant tests and build locally.
- Add or update API documentation and tests when API behavior changes.
- Check that authorization is enforced by the backend for every protected resource.
- Check that errors do not expose secrets, OTP values, password hashes, tokens, or private content.
- Confirm no credentials or user data were added to the change.
- Note any migration, configuration, compatibility, or operational implications.

For documentation-only changes, check links, filenames, code blocks, and consistency with related docs.

## Architecture Decision Records

Add a numbered ADR under `docs/adr/` for a significant decision. Include:

1. Title and status (`Proposed`, `Accepted`, `Superseded`, or `Deprecated`).
2. Context and the problem being solved.
3. Decision and alternatives considered.
4. Consequences, including trade-offs and follow-up work.
5. Conditions that would cause the decision to be revisited.

Do not mark a proposed ADR `Accepted` solely because the file exists. Record the decision after review.

## Security and reporting issues

Do not publish secrets or sensitive vulnerability details in a public issue. Avoid including real phone numbers, OTPs, tokens, private messages, or production records in examples. Use synthetic data for tests and bug reports. A private reporting process should be established before public launch.

## Local development

The backend has not yet been added. When it is introduced, its README or this file should document the required Java version, Maven Wrapper commands, configuration variables, database setup, tests, and local run instructions. Prefer the checked-in Maven Wrapper over depending on a developer-specific Maven installation.
