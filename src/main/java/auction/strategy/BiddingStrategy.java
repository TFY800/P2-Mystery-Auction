package auction.strategy;

public interface BiddingStrategy {
    // Return -1 to withdraw, otherwise propose the minimum legal bid.
    int decideBid(int minimumBid, int estimatedValue, int budget);
}
