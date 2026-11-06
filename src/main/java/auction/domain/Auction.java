package auction.domain;

import auction.exception.InvalidBidException;
import java.util.ArrayList;
import java.util.List;

public class Auction {
    public static final int OPENING_BID = 50;
    public static final int MIN_INCREMENT = 10;
    private final AuctionItem item;
    private final int[] budgets;
    private final boolean[] withdrawn = new boolean[3];
    private final List<BidRecord> bids = new ArrayList<>();
    private int turn;
    private int highestBidder = -1;
    private int price;
    private boolean closed;
    public Auction(AuctionItem item, int playerBudget, int firstBudget, int secondBudget) {
        if (item == null || playerBudget < 0 || firstBudget < 0 || secondBudget < 0) {
            throw new IllegalArgumentException("Invalid auction.");
        }
        this.item = item; budgets = new int[]{playerBudget, firstBudget, secondBudget};
    }
    private void validateActor(int bidder) {
        if (closed) throw new InvalidBidException("Auction is closed.");
        if (bidder < 0 || bidder > 2) throw new InvalidBidException("Unknown bidder.");
        if (withdrawn[bidder]) throw new InvalidBidException("Bidder has withdrawn.");
        if (bidder != turn) throw new InvalidBidException("It is not your turn.");
    }
    public void placeBid(int bidder, int amount) {
        validateActor(bidder);
        if ((long) amount < minimumBidLong()) throw new InvalidBidException("Bid increase is too small.");
        if (amount > budgets[bidder]) throw new InvalidBidException("Insufficient funds.");
        bids.add(new BidRecord(bidder, amount));
        price = amount; highestBidder = bidder; advance();
    }
    public void withdraw(int bidder) {
        validateActor(bidder);
        withdrawn[bidder] = true; advance();
    }
    private void advance() {
        int eligible = 0;
        for (int i = 0; i < 3; i++) if (!withdrawn[i] && i != highestBidder) eligible++;
        if (eligible == 0) { closed = true; return; }
        do { turn = (turn + 1) % 3; } while (withdrawn[turn] || turn == highestBidder);
    }
    private long minimumBidLong() { return highestBidder == -1 ? OPENING_BID : (long) price + MIN_INCREMENT; }
    public int getMinimumBid() { return (int) Math.min(Integer.MAX_VALUE, minimumBidLong()); }
    public int getTurn() { return turn; }
    public int getPrice() { return price; }
    public int getHighestBidder() { return highestBidder; }
    public boolean isClosed() { return closed; }
    public AuctionItem getItem() { return item; }
    public List<BidRecord> getBids() { return new ArrayList<>(bids); }
    public AuctionRecord result(int round) {
        if (!closed) throw new IllegalStateException("Auction is still open.");
        return new AuctionRecord(round, highestBidder, price, item.getHiddenValue(), item.getIdentity());
    }
}
