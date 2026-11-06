package auction.strategy;

public class CautiousStrategy implements BiddingStrategy {
    @Override public int decideBid(int minimumBid, int estimatedValue, int budget) {
        return minimumBid > 0 && minimumBid <= budget && minimumBid <= estimatedValue * 7L / 10 ? minimumBid : -1;
    }
}
