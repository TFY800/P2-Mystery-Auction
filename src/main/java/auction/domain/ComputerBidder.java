package auction.domain;

import auction.strategy.BiddingStrategy;
import auction.strategy.CautiousStrategy;
import java.util.Random;

public class ComputerBidder {
    private BiddingStrategy strategy;
    private final int budget;
    private final int estimate;
    private boolean switched;
    public ComputerBidder(BiddingStrategy strategy, int budget, ItemClues clues, Random random) {
        if (strategy == null || budget < 0 || clues == null || random == null) {
            throw new IllegalArgumentException("Invalid computer bidder.");
        }
        this.strategy = strategy; this.budget = budget;
        estimate = clues.guideValue() * (80 + random.nextInt(41)) / 100;
    }
    public void setStrategy(BiddingStrategy strategy) {
        if (strategy == null) throw new IllegalArgumentException("Strategy is required.");
        this.strategy = strategy;
    }
    public int decideBid(int minimumBid) {
        // Switch once when bidding would commit more than 40% of the round budget.
        if (!switched && minimumBid > budget * 4L / 10) {
            setStrategy(new CautiousStrategy()); switched = true;
        }
        return strategy.decideBid(minimumBid, estimate, budget);
    }
    public boolean hasSwitched() { return switched; }
}
