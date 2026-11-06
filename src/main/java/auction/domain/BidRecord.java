package auction.domain;

public class BidRecord {
    private final int bidder;
    private final int amount;
    public BidRecord(int bidder, int amount) {
        if (bidder < 0 || bidder > 2 || amount < Auction.OPENING_BID) {
            throw new IllegalArgumentException("Invalid bid record.");
        }
        this.bidder = bidder; this.amount = amount;
    }
    public int getBidder() { return bidder; }
    public int getAmount() { return amount; }
}
