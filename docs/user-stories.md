# User Stories

All eight stories are implemented. Acceptance is verified by `GameRulesTest`, `PersistenceTest` and `ConsoleTest`; see [verification](verification.md).

## US-01: Start a game

As a player, I want to enter my name and start with $1,000 so that every new game has a clear starting point.

Acceptance: blank names are rejected; a new session begins at round 1 with empty history.

## US-02: Inspect clues

As a player, I want to inspect an item's size, weight, age and condition so that I can estimate its value.

Acceptance: clues are visible before bidding; hidden identity and appraisal remain undisclosed; clues and appraisal share the same generated attributes.

## US-03: Bid or withdraw

As a player, I want to raise the bid or leave the auction so that I can control risk.

Acceptance: a bid must be higher than the current price and within my balance; withdrawn participants cannot return; the highest bidder cannot raise their own bid; invalid actions leave state unchanged.

## US-04: Compete with computers

As a player, I want opponents with different bidding styles so that decisions vary between auctions.

Acceptance: cautious and aggressive strategies are called through the same interface; both obey budgets; computers act on uncertain estimates; the auction always terminates.

## US-05: Reveal and settle

As a player, I want to see what I bought and its resale value so that I understand the result.

Acceptance: a player victory deducts the price and adds resale proceeds exactly once; $200 paid and $280 received changes $1,000 to $1,080; $150 received changes it to $950; losing changes no money.

## US-06: Review history

As a player, I want to review and sort round results so that I can identify my best decisions.

Acceptance: each completed round has an outcome; sorting by profit preserves the original chronological history; non-player wins are clearly marked and have zero player profit.

## US-07: Save and resume

As a player, I want to save between rounds so that I can continue later.

Acceptance: restoring reproduces player balance, next round and history; missing or corrupt files produce a clear message; no round is settled twice.

## US-08: Finish and compare

As a player, I want a final result and leaderboard so that I can compare runs.

Acceptance: finish after 10 rounds or unaffordable opening bid; record each completed game once; sort scores by final balance; persist scores between program runs.
