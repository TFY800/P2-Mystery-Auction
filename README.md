# Mystery Auction

A complete local single-player Java console game for the Programming 2 individual OOP project.
Players start with **$1,000**, inspect clues, and compete against two computer bidders for up to **10 rounds**.
All money uses integer dollars. Winning items are revealed and automatically resold immediately.

## Prerequisites and verified commands

Install **JDK 17** and **Maven 3.9+**. Set `JAVA_HOME` to the JDK and put Maven on `PATH`.
The first build needs access to Maven Central to download Maven plugins and JUnit; gameplay uses no network.
From the repository root:

```sh
java -version
mvn -version
mvn clean verify
java -jar target/mystery-auction-1.0.0.jar
mvn test
```

`mvn clean verify` compiles, runs all tests and packages the executable JAR.
`mvn test` runs just the build stages through testing. These commands were verified using
Temurin 17.0.21 and Maven 3.9.11, including an isolated checkout. See [verification](docs/verification.md).

## Playing

Main menu: `n` new game, `l` load, `b` leaderboard, `q` quit.
Enter a nonblank name. Between rounds: `p` play, `h` chronological history,
`o` history sorted by descending player profit, `s` save and return to the menu.
During an auction, enter a whole-dollar bid or `w` to withdraw permanently.
Opening bid is **$50**, minimum increase is **$10**; larger increases are allowed.
Invalid input keeps your turn. Order is player, computer 1, computer 2, repeating while
skipping withdrawn participants and the current highest bidder.
Computers finish even after you withdraw. An all-withdrawn lot is unsold.

Example (items and computer decisions vary):

```text
Mystery Auction | Opening bid: $50 | Minimum increment: $10
[n] New game  [l] Load  [b] Leaderboard  [q] Quit
n
Player name:
Alice
Next round: 1/10 | Balance: $1000
[p] Play round  [h] History  [o] History by profit  [s] Save and exit
p
Round 1/10 | Sealed instrument case | Size: 1/3 | Weight: 1 kg | Approximate age: 83 years | Material: wood | Condition: 1/5
Current bid: $0 | Minimum: $50 | Enter bid or [w] Withdraw
200
```

If the other bidders withdraw here, the item resells for $280:
**$1,000 - $200 + $280 = $1,080**, with $80 profit. Profit is never added twice.
A computer win or unsold lot leaves player funds unchanged. Every result shows identity,
bid, resale value, player profit and current balance; completion shows the final balance.

## Implemented requirements

- Three item templates; randomized size, weight, approximate age, material and condition.
- Fixed value derived from clues; computer estimates have 80–120% uncertainty.
- Validated bids, fixed turns, highest-bidder skipping, permanent withdrawal and finite auctions.
- Session-owned auctions and exactly-once settlement, including after load.
- Ten-round sessions and an early-end check when funds are below the opening bid.
- Chronological history and a separately sorted copy; persistent leaderboard sorted by final balance.
- Between-round save/load, explicit overwrite confirmation, corrupt-data validation and session-ID score deduplication.
- Private fields, validated constructors, custom domain exception and ordinary Java collections.
- JUnit 5 tests with seeded randomness and temporary persistence directories.

## Strategy pattern

`BiddingStrategy` is the decision interface. `CautiousStrategy` bids up to 70% of an
uncertain estimate, `AggressiveStrategy` up to 110%; both cap bids by budget.
`ComputerBidder` delegates through the interface. Each computer has a fresh $1,000
round budget; these are independent of player funds. Computer 1 starts cautious,
computer 2 aggressive. When the minimum bid exceeds 40% of its budget, a computer
replaces its policy with `CautiousStrategy` at runtime, once per round. The aggressive
computer therefore actually changes behavior. `Auction` independently validates every proposal.

## Persistence

Runtime files are ignored under `data/`: `save.txt` and `leaderboard.txt`.
These are versioned UTF-8 tab-separated text files. Free text is Base64 encoded so names
containing tabs, newlines or non-ASCII characters cannot break the format.
Save identity, balance, next round, session UUID and history are preserved. Loading
replays the stored results to validate balance, affordability and sequence. It never
silently creates a new game after an error. A save is replaced only after typing `yes`.
Writes use a temporary file followed by replacement; try-with-resources closes files.
A leaderboard write failure offers retry or saving the completed session for later retry.
Loading an old save cannot add a second leaderboard entry for the same session UUID.

## Documentation

- [Project specification and requirement mapping](docs/project-specification.md)
- [User stories and acceptance criteria](docs/user-stories.md)
- [Actual UML, design decisions and file format](docs/design.md)
- [Development backlog and workflow](docs/development-plan.md)
- [Verification evidence](docs/verification.md)
- [Release notes](docs/release-notes.md)

## Scope and limitations

Local single-player console only; fictional appraisals, no real payments, GUI or networking.
Saves are between rounds, including before the first round; active auctions cannot be saved.
RNG state is not saved, so future items after loading may differ. One save slot; no encryption
or tamper-proofing. Run only one process per data directory; concurrent writes and sudden
power-loss durability are outside scope. An older save remains until explicitly overwritten.
The score from the first completion of a UUID is retained if that older save is replayed.
EOF between rounds exits without saving; EOF during an auction withdraws the player and
settles that round. The minimum generated resale is $116, so ordinary play cannot fall below
the $50 opening bid; the early-end condition is implemented and tested with a valid restored
low-value history for future template changes. Opponent budgets reset each round, and
balance/game difficulty has only limited seeded playtesting.

## Contributor

TFY800

## License

[MIT](LICENSE), copyright 2026 TFY800.
