package auction.domain;

public class AuctionRecord {
    private final int round;
    private final int winner;
    private final int winningBid;
    private final int resaleValue;
    private final String identity;
    public AuctionRecord(int round, int winner, int winningBid, int resaleValue, String identity) {
        if (round < 1 || round > 10 || winner < -1 || winner > 2 || resaleValue < 1
                || identity == null || identity.isBlank()
                || (winner == -1 ? winningBid != 0 : winningBid < Auction.OPENING_BID)) {
            throw new IllegalArgumentException("Invalid auction result.");
        }
        this.round = round; this.winner = winner; this.winningBid = winningBid;
        this.resaleValue = resaleValue; this.identity = identity;
    }
    public int getRound() { return round; }
    public int getWinner() { return winner; }
    public int getWinningBid() { return winningBid; }
    public int getResaleValue() { return resaleValue; }
    public String getIdentity() { return identity; }
    public int getPlayerProfit() { return winner == 0 ? resaleValue - winningBid : 0; }
    @Override public String toString() {
        String outcome = winner == -1 ? "Unsold" : winner == 0 ? "Player won" : "Computer " + winner + " won";
        return "Round " + round + ": " + outcome + " | " + identity + " | Bid: $" + winningBid
                + " | Resale value: $" + resaleValue + " | Player profit: $" + getPlayerProfit();
    }
}
