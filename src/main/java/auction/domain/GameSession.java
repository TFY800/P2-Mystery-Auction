package auction.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class GameSession {
    private final String id;
    private final Player player;
    private final List<AuctionRecord> history = new ArrayList<>();
    private Auction activeAuction;
    public GameSession(String name) { id = UUID.randomUUID().toString(); player = new Player(name); }
    private GameSession(String id, Player player, List<AuctionRecord> records) {
        this.id = UUID.fromString(id).toString(); this.player = player; history.addAll(records);
    }
    public static GameSession restore(String id, String name, int balance, int nextRound, List<AuctionRecord> records) {
        if (records == null || records.size() > 10 || nextRound != records.size() + 1) {
            throw new IllegalArgumentException("Invalid saved round.");
        }
        Player replay = new Player(name);
        for (int i = 0; i < records.size(); i++) {
            AuctionRecord r = records.get(i);
            if (r == null || r.getRound() != i + 1 || replay.getBalance() < Auction.OPENING_BID) {
                throw new IllegalArgumentException("Invalid saved history.");
            }
            if (r.getWinner() == 0) replay.settle(r.getWinningBid(), r.getResaleValue());
        }
        if (balance != replay.getBalance()) throw new IllegalArgumentException("Saved balance does not match history.");
        return new GameSession(id, replay, records);
    }
    public Auction startAuction(AuctionItem item) {
        if (isComplete() || activeAuction != null) throw new IllegalStateException("Cannot start another auction.");
        activeAuction = new Auction(item, player.getBalance(), 1000, 1000);
        return activeAuction;
    }
    public AuctionRecord settle(Auction auction) {
        if (auction == null || auction != activeAuction || !auction.isClosed()) {
            throw new IllegalStateException("No unsettled closed auction.");
        }
        AuctionRecord result = auction.result(getNextRound());
        if (result.getWinner() == 0) player.settle(result.getWinningBid(), result.getResaleValue());
        history.add(result); activeAuction = null;
        return result;
    }
    public String getId() { return id; }
    public Player getPlayer() { return player; }
    public int getNextRound() { return history.size() + 1; }
    public boolean isBetweenRounds() { return activeAuction == null; }
    public boolean isComplete() { return history.size() == 10 || player.getBalance() < Auction.OPENING_BID; }
    public List<AuctionRecord> getHistory() { return new ArrayList<>(history); }
    public List<AuctionRecord> getHistoryByProfit() {
        List<AuctionRecord> sorted = getHistory();
        sorted.sort(Comparator.comparingInt(AuctionRecord::getPlayerProfit).reversed());
        return sorted;
    }
}
