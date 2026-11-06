package auction.strategy;

public class AggressiveStrategy implements BiddingStrategy {
    @Override public int decideBid(int minimumBid, int estimatedValue, int budget) {
        return minimumBid > 0 && minimumBid <= budget && minimumBid <= estimatedValue * 11L / 10 ? minimumBid : -1;
    }
}
