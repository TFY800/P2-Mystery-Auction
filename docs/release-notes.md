# v1.0.0 — Mystery Auction

First complete Programming 2 release, 2026-11-06.

- Play 10 console auction rounds with a named player starting at $1,000.
- Inspect generated clues correlated with a fixed hidden value; reveal and resell after bidding.
- Compete with cautious/aggressive Strategy opponents with uncertain estimates and runtime switching.
- Enforce turn order, budgets, minimum increases, permanent withdrawal and exactly-once settlement.
- Review chronological/profit-sorted history and a persistent balance-sorted leaderboard.
- Save/load between rounds with overwrite confirmation, validated UTF-8 text and UUID score deduplication.
- Java 17 Maven executable JAR, JUnit 5 rule/persistence/console coverage and standalone smoke script.
- Updated UML, requirement mapping, stories, verified commands and MIT license (TFY800).

Build: `mvn clean verify`. Run: `java -jar target/mystery-auction-1.0.0.jar`.
Test: `mvn test`. Optional JAR smoke test: `python3 scripts/smoke-test.py`.

Limitations: single process, one local save slot, no mid-auction saves, no saved RNG state,
fictional valuation and simulated bidders; see README for full behavior and limitations.
