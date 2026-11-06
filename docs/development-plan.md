# Development Plan

## Backlog

- [ ] Share the repository URL with the instructor before implementation.
- [x] Select a JDK version; add Maven configuration and console entry point.
- [x] Implement encapsulated player funds, items and auction records.
- [x] Implement bid validation, turn order, withdrawal and auction closure.
- [x] Add cautious/aggressive strategies and demonstrate runtime replacement.
- [x] Generate coherent item clues and uncertain opponent estimates.
- [x] Complete a playable round and exactly-once settlement.
- [x] Add 10-round sessions, history sorting and leaderboard.
- [x] Add save/load with corrupt-file handling and resource management.
- [x] Add JUnit tests for invalid bids, settlement, closure and persistence.
- [x] Verify a clean checkout; document actual build/run/test commands.
- [x] Update UML to match code; record final limitations.
- [x] Select a license and add LICENSE.
- Release gate: publish v1.0.0 only after the checks in [verification](verification.md) pass; see GitHub Releases for current publication status.

## Workflow

Use main as the stable branch. Create focused feature or fix branches and merge them through pull requests. Use meaningful commit messages, such as feat: validate auction bids or test: prevent duplicate settlement. Track implementation tasks as GitHub Issues. Do not invent historical commits or mark unfinished features as complete.

## Suggested increments

1. Project setup and one console action.
2. Core auction rules and tests.
3. Strategies and one playable round.
4. Sessions, persistence and sorting.
5. Documentation, clean-checkout verification and release.

## Release checklist

The final release must contain source, build configuration, verified instructions, passing JUnit tests, matching UML, pattern explanation, user stories, license, issue and branch history, a final tag and release notes. The implementation is complete; see verification.md for executed checks. External workflow status is recorded below.

## Actual implementation workflow

- Backlog issues: [#2 core game](https://github.com/TFY800/P2-Mystery-Auction/issues/2)
  and [#3 persistence/documentation/verification](https://github.com/TFY800/P2-Mystery-Auction/issues/3).
- Feature branch: `feature/complete-mystery-auction`.
- Commits reflect actual work: complete implementation/tests, then documentation and reproducible smoke verification.
- A pull request targets main; no force-push or fabricated intermediate history.
- The original planning commits and useful requirements/user stories are preserved.
- Instructor URL submission remains the contributor's administrative task.
- Release notes are in `docs/release-notes.md`; v1.0.0 is created only after clean verification.
