package auction;

import auction.domain.*;
import auction.exception.InvalidBidException;
import auction.service.*;
import auction.strategy.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class GameRulesTest {
    static AuctionItem item(int value) {
        // wood: 40 + 20 + 4 + 2*age + 30 + 20 = 114 + 2*age
        return new AuctionItem(new ItemClues("Case", 1, 1, (value - 114) / 2, "wood", 1), "Toy");
    }
    static AuctionRecord win(GameSession session, int price, int value) {
        Auction a = session.startAuction(item(value));
        a.placeBid(0, price); a.withdraw(1); a.withdraw(2);
        return session.settle(a);
    }
    static void unsold(GameSession session) {
        Auction a = session.startAuction(item(280));
        a.withdraw(0); a.withdraw(1); a.withdraw(2); session.settle(a);
    }
    @Test void initialFundsAndConstructorValidation() {
        GameSession s = new GameSession("Alice");
        assertEquals(1000, s.getPlayer().getBalance()); assertEquals(1, s.getNextRound());
        assertTrue(s.getHistory().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> new Player("  "));
        assertThrows(IllegalArgumentException.class, () -> new Player(null));
        assertThrows(IllegalArgumentException.class, () -> new Player("A", -1));
        assertThrows(IllegalArgumentException.class, () -> new Auction(null, 100, 100, 100));
        assertThrows(IllegalArgumentException.class, () -> new Auction(item(280), -1, 100, 100));
        assertThrows(IllegalArgumentException.class, () -> new ItemClues("", 1, 1, 1, "wood", 1));
        assertThrows(IllegalArgumentException.class, () -> new ItemClues("Case", 0, 1, 1, "wood", 1));
        assertThrows(IllegalArgumentException.class, () -> new AuctionRecord(0, -1, 0, 280, "Toy"));
        assertThrows(IllegalArgumentException.class, () -> new AuctionRecord(1, -1, 50, 280, "Toy"));
    }
    @Test void rejectedBidsDoNotChangeStateOrConsumeTurn() {
        Auction a = new Auction(item(280), 100, 100, 100);
        for (int amount : new int[]{-1, 0, 49, 101, Integer.MAX_VALUE}) {
            assertThrows(InvalidBidException.class, () -> a.placeBid(0, amount));
            assertEquals(0, a.getPrice()); assertEquals(0, a.getTurn());
            assertEquals(-1, a.getHighestBidder()); assertTrue(a.getBids().isEmpty());
        }
        assertThrows(InvalidBidException.class, () -> a.placeBid(1, 50));
        assertThrows(InvalidBidException.class, () -> a.withdraw(3));
        assertThrows(InvalidBidException.class, () -> a.withdraw(1));
        a.placeBid(0, 50);
        assertThrows(InvalidBidException.class, () -> a.placeBid(1, 59));
        assertEquals(1, a.getTurn()); assertEquals(50, a.getPrice()); assertEquals(1, a.getBids().size());
        a.placeBid(1, 60); assertEquals(2, a.getTurn());
    }
    @Test void turnOrderSkipsHighestAndWithdrawalIsPermanent() {
        Auction a = new Auction(item(280), 1000, 1000, 1000);
        a.placeBid(0, 50); a.withdraw(1);
        assertThrows(InvalidBidException.class, () -> a.placeBid(1, 60));
        a.placeBid(2, 60); assertEquals(0, a.getTurn());
        a.placeBid(0, 70); assertEquals(2, a.getTurn());
        assertThrows(InvalidBidException.class, () -> a.placeBid(0, 80));
        a.withdraw(2); assertTrue(a.isClosed());
        assertEquals(0, a.result(1).getWinner());
        assertThrows(InvalidBidException.class, () -> a.placeBid(0, 80));
        assertThrows(InvalidBidException.class, () -> a.withdraw(0));
        assertEquals(3, a.getBids().size());
        a.getBids().clear(); assertEquals(3, a.getBids().size());
    }
    @Test void everyoneWithdrawsIsUnsold() {
        Auction a = new Auction(item(280), 100, 100, 100);
        assertThrows(IllegalStateException.class, () -> a.result(1));
        a.withdraw(0); a.withdraw(1); a.withdraw(2);
        assertTrue(a.isClosed()); assertEquals(-1, a.result(1).getWinner());
        assertEquals(0, a.result(1).getWinningBid());
    }
    @Test void computerCompletionAfterPlayerWithdrawalIsFinite() {
        for (int seed = 0; seed < 200; seed++) {
            GameSession session = new GameSession("A"); GameService game = new GameService(new Random(seed));
            Auction a = game.startRound(session); a.withdraw(0); game.playComputers(a);
            assertTrue(a.isClosed()); assertTrue(a.getBids().size() <= 96);
            int previous = 40;
            for (BidRecord bid : a.getBids()) {
                assertTrue(bid.getAmount() >= previous + 10); assertTrue(bid.getAmount() <= 1000);
                assertNotEquals(0, bid.getBidder()); previous = bid.getAmount();
            }
            session.settle(a); assertEquals(1000, session.getPlayer().getBalance());
        }
    }
    @Test void finiteBudgetsTerminateEvenWithAlwaysRaiseParticipants() {
        Auction a = new Auction(item(280), 90, 120, 150);
        int actions = 0;
        while (!a.isClosed()) {
            int turn = a.getTurn(); int budget = new int[]{90, 120, 150}[turn];
            if (a.getMinimumBid() <= budget) a.placeBid(turn, a.getMinimumBid()); else a.withdraw(turn);
            assertTrue(++actions < 20);
        }
        assertEquals(2, a.getHighestBidder());
    }
    @Test void profitLossAndExactlyOnceSettlement() {
        GameSession s = new GameSession("A");
        Auction a = s.startAuction(item(280)); a.placeBid(0, 200);
        assertEquals(1000, s.getPlayer().getBalance());
        assertThrows(IllegalStateException.class, () -> s.settle(a));
        assertThrows(IllegalStateException.class, () -> s.startAuction(item(280)));
        a.withdraw(1); a.withdraw(2);
        assertEquals(80, s.settle(a).getPlayerProfit()); assertEquals(1080, s.getPlayer().getBalance());
        assertThrows(IllegalStateException.class, () -> s.settle(a)); assertEquals(1080, s.getPlayer().getBalance());
        assertEquals(-50, win(s, 200, 150).getPlayerProfit()); assertEquals(1030, s.getPlayer().getBalance());
        unsold(s); assertEquals(1030, s.getPlayer().getBalance());
        Auction other = new Auction(item(280), 1000, 1000, 1000);
        other.withdraw(0); other.withdraw(1); other.withdraw(2);
        assertThrows(IllegalStateException.class, () -> s.settle(other));
    }
    @Test void completionAndEarlyEnd() {
        GameSession s = new GameSession("A");
        for (int i = 0; i < 10; i++) unsold(s);
        assertTrue(s.isComplete()); assertEquals(11, s.getNextRound());
        assertThrows(IllegalStateException.class, () -> s.startAuction(item(280)));
        GameSession early = GameSession.restore(java.util.UUID.randomUUID().toString(), "C", 1, 2,
                List.of(new AuctionRecord(1, 0, 1000, 1, "Low-value item")));
        assertTrue(early.isComplete());
        assertThrows(IllegalStateException.class, () -> early.startAuction(item(280)));
    }
    @Test void historySortingPreservesChronology() {
        GameSession s = new GameSession("A"); win(s, 300, 150); win(s, 200, 280); unsold(s);
        List<AuctionRecord> sorted = s.getHistoryByProfit();
        assertEquals(2, sorted.get(0).getRound()); assertEquals(3, sorted.get(1).getRound());
        assertEquals(1, s.getHistory().get(0).getRound()); sorted.clear();
        assertEquals(3, s.getHistory().size());
    }
    @Test void bothStrategiesRespectEstimatesAndBudgets() {
        BiddingStrategy cautious = new CautiousStrategy(); BiddingStrategy aggressive = new AggressiveStrategy();
        assertEquals(70, cautious.decideBid(70, 100, 100)); assertEquals(-1, cautious.decideBid(71, 100, 100));
        assertEquals(110, aggressive.decideBid(110, 100, 200)); assertEquals(-1, aggressive.decideBid(111, 100, 200));
        for (BiddingStrategy strategy : new BiddingStrategy[]{cautious, aggressive}) {
            assertEquals(-1, strategy.decideBid(50, 1000, 49));
            assertEquals(-1, strategy.decideBid(-1, 100, 100));
        }
    }
    @Test void runtimeReplacementChangesActualDecision() {
        ItemClues clues = new ItemClues("Case", 3, 20, 100, "brass", 5);
        ComputerBidder computer = new ComputerBidder(new AggressiveStrategy(), 1000, clues, new Random(1));
        assertEquals(390, computer.decideBid(390));
        assertEquals(-1, computer.decideBid(600)); assertTrue(computer.hasSwitched());
        ComputerBidder explicit = new ComputerBidder(new CautiousStrategy(), 2000, clues, new Random(1));
        assertEquals(-1, explicit.decideBid(600)); explicit.setStrategy(new AggressiveStrategy());
        assertEquals(600, explicit.decideBid(600));
        assertThrows(IllegalArgumentException.class, () -> explicit.setStrategy(null));
        ComputerBidder poor = new ComputerBidder(new AggressiveStrategy(), 49, clues, new Random(1));
        assertEquals(-1, poor.decideBid(50));
    }
    @Test void generationIsReproducibleAndCluesDetermineValue() {
        ItemGenerator first = new ItemGenerator(new Random(42)); ItemGenerator second = new ItemGenerator(new Random(42));
        for (int i = 0; i < 100; i++) {
            AuctionItem x = first.generate(); AuctionItem y = second.generate();
            assertEquals(x.getClues().toString(), y.getClues().toString());
            Auction a = new Auction(x, 1000, 1000, 1000); a.withdraw(0); a.withdraw(1); a.withdraw(2);
            Auction b = new Auction(y, 1000, 1000, 1000); b.withdraw(0); b.withdraw(1); b.withdraw(2);
            assertEquals(x.getClues().guideValue(), a.result(1).getResaleValue());
            assertEquals(a.result(1).getResaleValue(), b.result(1).getResaleValue());
        }
        assertTrue(new ItemClues("Case", 1, 1, 2, "wood", 1).guideValue()
                > new ItemClues("Case", 1, 1, 1, "wood", 1).guideValue());
    }
}
