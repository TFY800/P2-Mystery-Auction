# Development Plan

## Backlog

- [ ] Share the repository URL with the instructor before implementation.
- [ ] Select a JDK version; add Maven configuration and console entry point.
- [ ] Implement encapsulated player funds, items and auction records.
- [ ] Implement bid validation, turn order, withdrawal and auction closure.
- [ ] Add cautious/aggressive strategies and demonstrate runtime replacement.
- [ ] Generate coherent item clues and uncertain opponent estimates.
- [ ] Complete a playable round and exactly-once settlement.
- [ ] Add 10-round sessions, history sorting and leaderboard.
- [ ] Add save/load with corrupt-file handling and resource management.
- [ ] Add JUnit tests for invalid bids, settlement, closure and persistence.
- [ ] Verify a clean checkout; document actual build/run/test commands.
- [ ] Update UML to match code; record final limitations.
- [ ] Select a license and add LICENSE.
- [ ] Tag the completed version and publish release notes.

## Workflow

Use main as the stable branch. Create focused feature or fix branches and merge them through pull requests. Use meaningful commit messages, such as feat: validate auction bids or test: prevent duplicate settlement. Track implementation tasks as GitHub Issues. Do not invent historical commits or mark unfinished features as complete.

## Suggested increments

1. Project setup and one console action.
2. Core auction rules and tests.
3. Strategies and one playable round.
4. Sessions, persistence and sorting.
5. Documentation, clean-checkout verification and release.

## Release checklist

The final release must contain source, build configuration, verified instructions, passing JUnit tests, matching UML, pattern explanation, user stories, license, issue and branch history, a final tag and release notes. This planning revision is not a final submission.
