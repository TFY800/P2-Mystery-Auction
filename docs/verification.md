# Verification evidence

Executed 2026-11-06 (Europe/Budapest), using Temurin **17.0.21+9** and Maven **3.9.11**
on macOS arm64. Java 17 was installed under a task-local tools directory because the
host initially had only Java 21; Maven was also installed locally. No runtime tools
or generated data are committed.

## Commands actually executed

With `JAVA_HOME` pointing to JDK 17 and Maven on `PATH`:

```sh
java -version
mvn -version
mvn -B -ntp clean verify
java -jar target/mystery-auction-1.0.0.jar
mvn -B -ntp test
python3 scripts/smoke-test.py
```

`-B -ntp` controls Maven logging only; the README commands invoke the same build phases.
The JAR launch was checked with `q` piped into stdin, as well as the full flows below.
The Maven test and build commands succeeded in the implementation checkout and in a
fresh `git clone --no-local` checkout at `5485b14`, with no target directory or game data.
The source and smoke script in that commit are the release implementation; subsequent
documentation records this verification. `git status --short` was empty in the isolated
checkout after the checks (build output is ignored).

## Results

| Check | Actual result |
| --- | --- |
| `mvn clean verify`, isolated checkout | BUILD SUCCESS; executable JAR created |
| `mvn test`, isolated checkout | 20 tests; 0 failures, 0 errors, 0 skipped |
| `GameRulesTest` | 12 tests passed, including loops over 200 seeded computer auctions and 100 generated item pairs |
| `PersistenceTest` | 5 tests passed; every persistence file uses a JUnit temporary directory |
| `ConsoleTest` | 3 tests passed; menus/input/overwrite errors and resumed completion |
| Standalone JAR smoke, isolated checkout | PASS; three separate JVM processes, temporary data directory |
| Plain executable JAR launch and quit | Exit code 0, correct main menu |
| `git diff --check` | No whitespace errors |

Core coverage: initial funds and invalid constructors; opening/increment/budget checks;
unchanged state on rejected bids; order and highest-bidder skipping; permanent withdrawal;
unsold closure; finite auctions; computer completion after player withdrawal; both policies
and runtime switching; profit/loss; exactly-once settlement; 10-round/early completion;
copy-based history sorting; reproducible clue-based appraisal.

Persistence coverage: Unicode/tab/newline names, UUID and history round trips, explicit
overwrite, mid-auction refusal, missing/truncated/corrupt files, bad UTF-8/Base64, invalid
sequence/balance, leaderboard ordering, completion requirements, corruption preservation,
and duplicate-score prevention after loading.

## Standalone console smoke sequence

`scripts/smoke-test.py` launches the packaged JAR in a temporary directory, then:

1. Creates Smoke with $1,000; enters malformed text, a $49 bid, and an unaffordable $1,001 bid.
2. Places a valid $50 bid, withdraws on the next player turn, lets computers finish, saves and exits.
3. Starts a **new JVM**, loads round 2 with $1,000, opens both history views, withdraws from
   each of the remaining nine auctions, and completes at $1,000.
4. Starts a **third JVM**, replays the old save to completion, and verifies the leaderboard
   still contains exactly one score entry for that session UUID.

The first smoke-script invocation exposed a relative JAVA_HOME path issue in the script;
it was fixed by resolving the path before changing the subprocess working directory.
The corrected script passed in both checkouts. No game test failures remained.

## Limits of verification

Tests exercise application behavior; they do not prove crash durability, concurrent access,
tamper resistance or balance difficulty. A Maven cache was available during isolated
verification; a first build still requires Maven Central connectivity. No instructor
notification was attempted because no recipient was provided.
