# Project Specification

## Problem statement

Mystery Auction gives players a short decision-making game about evaluating incomplete information and managing a limited budget. The target user is a player who enjoys bidding, collecting and risk assessment. The goal is to finish 10 rounds with as much money as possible.

## Rules

1. A new player receives $1,000. Use integer dollars throughout.
2. Each round generates an item from a template, then derives visible clues and a fixed hidden appraisal value from its attributes. Clues must correlate with value; price must not be independent random noise.
3. Two computer bidders receive budgets and uncertain estimates based on clues. They do not receive the hidden appraisal value.
4. Participants act in a fixed order. The current highest bidder skips their turn. Others must submit a higher affordable bid or permanently withdraw from this round.
5. A participant cannot bid after withdrawing. Invalid input does not consume a turn or change the auction state.
6. Once all opponents of the highest bidder have withdrawn, the auction closes. If everyone withdraws before any bid, the lot is unsold.
7. If the player withdraws, computer bidders finish the auction normally. Their finite budgets and strictly increasing bids ensure termination.
8. Money is not deducted on every bid. Only a player victory triggers payment and automatic resale. Apply settlement exactly once.
9. Profit = appraisal value - winning bid. New balance = old balance + profit. Computer victories and unsold lots leave the player's money unchanged.
10. End after 10 rounds, or early when the player cannot afford the minimum legal opening bid. Record the final score once.
11. Save only after a completed round. Persist the next round number, player and history. Resume without replaying settled rounds.

## Requirement mapping (implemented)

| Assignment requirement | Implemented evidence |
| --- | --- |
| Encapsulation | Private fields; validated constructors; controlled payment and bidding methods |
| Abstraction | BiddingStrategy interface |
| Inheritance and runtime polymorphism | Two strategy implementations invoked through the interface; custom exception extends RuntimeException |
| Relationships and multiplicities | Session/player/history; auction/item/bidders/bids |
| Generic collections | List of auction records and bidders |
| Sorting | History by profit; leaderboard by final balance |
| Validation and custom exception | InvalidBidException; malformed console input handled separately |
| Persistence | Save and restore game data; resource-safe file handling |
| Recognized design pattern | Strategy with documented participants and runtime replacement |
| Console interface | Text clues, menu, bid input and settlement output |
| Build | Maven project reproducible from a clean checkout |
| Testing | JUnit coverage of rules, settlement and save round trips |
| Documentation | README, user stories, UML, decisions and limitations |
| Repository workflow | Issues, feature branches, pull requests and meaningful commits |
| Final delivery | License, verified commands, final tag and release notes |

The assignment describes tests as optional in some sections but includes passing JUnit tests in the final submission package. This project will include tests.

## Concrete rule choices

Opening bid $50; minimum increase $10. Each round has three participants with IDs
0 (player), 1 and 2 (computers). Computers each have a $1,000 round budget. The game
ends after ten results or when balance is below $50. Because generated resale is at
least $116, the early-end branch is reserved for future lower-value templates.

`GameRulesTest` checks domain rules, estimates, runtime strategy replacement, termination,
settlement and sorting. `PersistenceTest` checks text round trips, replay validation,
missing/corrupt files and score deduplication. `ConsoleTest` exercises menus, malformed
input, overwrite confirmation and full sessions. See [verification](verification.md).

The repository URL still needs to be shared with the instructor by TFY800; no instructor
contact was supplied. This administrative action is outside the implemented game.

For a real menu transcript and verified commands, see [README](../README.md).
