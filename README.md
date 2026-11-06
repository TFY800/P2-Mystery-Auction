# Mystery Auction

A Java console game for the Programming 2 individual OOP project.

Players start with **$1,000**, inspect clues about mystery items, compete against computer bidders, and discover whether their purchases make a profit or a loss after appraisal and automatic resale.

## Status

**Planning stage.** This repository currently contains the project specification and proposed design. Gameplay, persistence and tests are not implemented yet. No final release has been published.

## Planned gameplay

- Play up to 10 auction rounds against two computer bidders.
- Inspect clues including size, weight, approximate age, material and condition.
- Raise the bid or withdraw from the current auction.
- Pay only when winning; immediately resell the revealed item at its appraisal value.
- Review round results and sort history by profit.
- Save between rounds and resume later.
- Record completed games on a local leaderboard.

### Settlement example

Starting balance: $1,000. Winning bid: $200. Resale value: $280.

Final balance = $1,000 - $200 + $280 = **$1,080**. Round profit = **$80**.

Computer wins and unsold lots do not change the player's balance.

## Planned technology

Java, Maven, a console interface, file persistence and JUnit. The exact JDK version and dependency versions will be fixed when the Maven project is added. All game money uses integer dollars.

## Build, run and test

Not available yet: there is no Maven configuration or application entry point in this planning revision. Reproducible commands and prerequisites will be added and verified with the first working implementation.

## Design pattern

**Strategy** will separate computer bidding behavior from auction rules. `BiddingStrategy` defines the decision contract; `CautiousStrategy` and `AggressiveStrategy` provide different implementations; `ComputerBidder` delegates decisions to its strategy. Strategies can be changed at runtime, while `Auction` remains responsible for validating all bids.

## Documentation

- [Project specification and requirement mapping](docs/project-specification.md)
- [User stories and acceptance criteria](docs/user-stories.md)
- [Proposed UML and design decisions](docs/design.md)
- [Development backlog and workflow](docs/development-plan.md)

## Scope and limitations

The first version is a local single-player console game with simulated opponents. Appraisal is a fictional game mechanic. Saves are supported only between rounds. Networking, real payments and real email are outside the initial scope.

## Contributor

TFY800

## License

A license has not been selected yet. Add a LICENSE before final submission.
