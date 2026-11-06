package auction.service;

import auction.domain.Auction;
import auction.domain.ComputerBidder;
import auction.domain.GameSession;
import auction.strategy.AggressiveStrategy;
import auction.strategy.CautiousStrategy;
import java.util.Random;

public class GameService {
    private final Random random;
    private final ItemGenerator generator;
    private ComputerBidder first;
    private ComputerBidder second;
    public GameService(Random random) {
        if (random == null) throw new IllegalArgumentException("Random is required.");
        this.random = random; generator = new ItemGenerator(random);
    }
    public Auction startRound(GameSession session) {
        Auction auction = session.startAuction(generator.generate());
        first = new ComputerBidder(new CautiousStrategy(), 1000, auction.getItem().getClues(), random);
        second = new ComputerBidder(new AggressiveStrategy(), 1000, auction.getItem().getClues(), random);
        return auction;
    }
    public void playComputers(Auction auction) {
        while (!auction.isClosed() && auction.getTurn() != 0) {
            int bidder = auction.getTurn();
            ComputerBidder computer = bidder == 1 ? first : second;
            int proposal = computer.decideBid(auction.getMinimumBid());
            if (proposal == -1) auction.withdraw(bidder);
            else auction.placeBid(bidder, proposal);
        }
    }
}
