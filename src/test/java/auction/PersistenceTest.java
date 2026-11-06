package auction;

import auction.domain.*;
import auction.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PersistenceTest {
    @TempDir Path directory;
    @Test void roundTripPreservesIdentityMoneyHistoryAndNextRound() throws IOException {
        GameSession s = new GameSession("名字\tA\nB"); GameRulesTest.win(s, 200, 280); GameRulesTest.unsold(s);
        SaveRepository saves = new SaveRepository(directory.resolve("nested/save.txt")); saves.save(s, false);
        GameSession loaded = saves.load();
        assertEquals(s.getId(), loaded.getId()); assertEquals(s.getPlayer().getName(), loaded.getPlayer().getName());
        assertEquals(1080, loaded.getPlayer().getBalance()); assertEquals(3, loaded.getNextRound());
        assertEquals(s.getHistory().toString(), loaded.getHistory().toString());
        GameRulesTest.unsold(loaded); assertEquals(1080, loaded.getPlayer().getBalance());
        assertThrows(FileAlreadyExistsException.class, () -> saves.save(loaded, false));
        assertEquals(3, saves.load().getNextRound()); saves.save(loaded, true); assertEquals(4, saves.load().getNextRound());
    }
    @Test void missingAndCorruptSaveAreReportedWithoutReplacement() throws IOException {
        Path file = directory.resolve("save.txt"); SaveRepository saves = new SaveRepository(file);
        assertTrue(assertThrows(IOException.class, saves::load).getMessage().contains("missing"));
        String id = UUID.randomUUID().toString();
        String[] invalid = {"", "wrong", "MYSTERY-SAVE-1\n", "MYSTERY-SAVE-1\ninvalid\tQQ==\t1000\t1\n",
                "MYSTERY-SAVE-1\n" + id + "\t%%%\t1000\t1\n",
                "MYSTERY-SAVE-1\n" + id + "\t/w==\t1000\t1\n",
                "MYSTERY-SAVE-1\n" + id + "\tQQ==\t999\t1\n",
                "MYSTERY-SAVE-1\n" + id + "\tQQ==\t1000\t2\n",
                "MYSTERY-SAVE-1\n" + id + "\tQQ==\t1000\t1\nextra\n",
                "MYSTERY-SAVE-1\n" + id + "\tQQ==\t1080\t2\n2\t0\t200\t280\tVG95\n"};
        for (String text : invalid) {
            Files.writeString(file, text); assertThrows(IOException.class, saves::load, text);
            assertEquals(text, Files.readString(file));
        }
    }
    @Test void rejectsMidAuctionSavesAndInvalidRestore() {
        GameSession s = new GameSession("A"); s.startAuction(GameRulesTest.item(280));
        SaveRepository saves = new SaveRepository(directory.resolve("save.txt"));
        assertThrows(IllegalStateException.class, () -> saves.save(s, false));
        assertFalse(saves.exists());
        assertThrows(IllegalArgumentException.class, () -> GameSession.restore(s.getId(), "A", 1000, 1,
                List.of(new AuctionRecord(1, 0, 2000, 280, "Toy"))));
    }
    private GameSession completed(String name, boolean profit) {
        GameSession session = new GameSession(name);
        if (profit) GameRulesTest.win(session, 200, 280); else GameRulesTest.unsold(session);
        while (!session.isComplete()) GameRulesTest.unsold(session);
        return session;
    }
    @Test void leaderboardPersistsSortsAndDeduplicatesAfterLoading() throws IOException {
        LeaderboardRepository scores = new LeaderboardRepository(directory.resolve("scores.txt"));
        assertTrue(scores.load().isEmpty());
        assertThrows(IllegalStateException.class, () -> scores.record(new GameSession("A")));
        GameSession lower = completed("Low", false); GameSession higher = completed("高\t\n", true);
        assertTrue(scores.record(lower)); assertTrue(scores.record(higher));
        assertEquals(1080, scores.load().get(0).getBalance()); assertEquals("高\t\n", scores.load().get(0).getName());
        SaveRepository saves = new SaveRepository(directory.resolve("save.txt")); saves.save(higher, false);
        assertTrue(saves.load().isComplete()); assertFalse(scores.record(saves.load()));
        assertEquals(2, new LeaderboardRepository(directory.resolve("scores.txt")).load().size());
    }
    @Test void corruptLeaderboardIsNotOverwritten() throws IOException {
        Path file = directory.resolve("scores.txt"); Files.writeString(file, "bad data");
        LeaderboardRepository scores = new LeaderboardRepository(file);
        assertThrows(IOException.class, scores::load);
        assertThrows(IOException.class, () -> scores.record(completed("A", false)));
        assertEquals("bad data", Files.readString(file));
    }
}
