package auction.ui;

import auction.domain.Auction;
import auction.domain.AuctionRecord;
import auction.domain.GameSession;
import auction.exception.InvalidBidException;
import auction.persistence.LeaderboardRepository;
import auction.persistence.SaveRepository;
import auction.persistence.ScoreEntry;
import auction.service.GameService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final SaveRepository saves;
    private final LeaderboardRepository leaderboard;
    private final GameService game;
    public ConsoleUI(Scanner scanner, Path data, Random random) {
        this.scanner = scanner;
        saves = new SaveRepository(data.resolve("save.txt"));
        leaderboard = new LeaderboardRepository(data.resolve("leaderboard.txt"));
        game = new GameService(random);
    }
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new ConsoleUI(scanner, Path.of("data"), new Random()).run();
        }
    }
    private String read(String prompt) {
        System.out.println(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }
    public void run() {
        System.out.println("Mystery Auction | Opening bid: $50 | Minimum increment: $10");
        while (true) {
            String command = read("[n] New game  [l] Load  [b] Leaderboard  [q] Quit");
            if (command == null || command.equalsIgnoreCase("q")) return;
            try {
                if (command.equalsIgnoreCase("n")) {
                    String name = read("Player name:");
                    if (name == null) return;
                    play(new GameSession(name));
                } else if (command.equalsIgnoreCase("l")) play(saves.load());
                else if (command.equalsIgnoreCase("b")) showLeaderboard();
                else System.out.println("Unknown command.");
            } catch (IOException | IllegalArgumentException e) { System.out.println(e.getMessage()); }
        }
    }
    private void play(GameSession session) throws IOException {
        while (!session.isComplete()) {
            System.out.println("Next round: " + session.getNextRound() + "/10 | Balance: $" + session.getPlayer().getBalance());
            String command = read("[p] Play round  [h] History  [o] History by profit  [s] Save and exit");
            if (command == null) { System.out.println("Input ended. Session was not saved."); return; }
            if (command.equalsIgnoreCase("s")) {
                boolean overwrite = false;
                if (saves.exists()) {
                    String confirmation = read("Overwrite existing save? Type yes to confirm:");
                    if (confirmation == null || !confirmation.equalsIgnoreCase("yes")) {
                        System.out.println("Save cancelled."); continue;
                    }
                    overwrite = true;
                }
                try { saves.save(session, overwrite); System.out.println("Saved. Returning to menu."); return; }
                catch (IOException e) { System.out.println("Save failed: " + e.getMessage()); }
            } else if (command.equalsIgnoreCase("h") || command.equalsIgnoreCase("o")) {
                for (AuctionRecord r : command.equalsIgnoreCase("h") ? session.getHistory() : session.getHistoryByProfit()) {
                    System.out.println(r);
                }
            } else if (command.equalsIgnoreCase("p")) playRound(session);
            else System.out.println("Unknown command.");
        }
        System.out.println("Session complete. Final balance: $" + session.getPlayer().getBalance());
        // Allow retrying a failed score write or saving the completed session.
        while (true) {
            try {
                leaderboard.record(session); showLeaderboard(); return;
            } catch (IOException e) {
                System.out.println("Leaderboard failed: " + e.getMessage());
                String retry = read("[r] Retry  [s] Save completed session and exit  [q] Return without recording");
                if (retry == null || retry.equalsIgnoreCase("q")) return;
                if (retry.equalsIgnoreCase("s")) {
                    boolean overwrite = false;
                    if (saves.exists()) {
                        String answer = read("Overwrite existing save? Type yes to confirm:");
                        if (answer == null || !answer.equalsIgnoreCase("yes")) continue;
                        overwrite = true;
                    }
                    try { saves.save(session, overwrite); System.out.println("Saved completed session."); return; }
                    catch (IOException saveError) { System.out.println("Save failed: " + saveError.getMessage()); }
                }
            }
        }
    }
    private void playRound(GameSession session) {
        Auction auction = game.startRound(session);
        System.out.println("Round " + session.getNextRound() + "/10 | " + auction.getItem().getClues());
        while (!auction.isClosed()) {
            game.playComputers(auction);
            if (auction.isClosed()) break;
            String command = read("Current bid: $" + auction.getPrice() + " | Minimum: $" + auction.getMinimumBid()
                    + " | Enter bid or [w] Withdraw");
            try {
                if (command == null || command.equalsIgnoreCase("w")) auction.withdraw(0);
                else auction.placeBid(0, Integer.parseInt(command));
            } catch (NumberFormatException e) { System.out.println("Enter a whole-dollar amount or w."); }
            catch (InvalidBidException e) { System.out.println(e.getMessage()); }
        }
        System.out.println(session.settle(auction));
        System.out.println("Balance: $" + session.getPlayer().getBalance());
    }
    private void showLeaderboard() throws IOException {
        System.out.println("Leaderboard (highest final balance first):");
        for (ScoreEntry score : leaderboard.load()) System.out.println(score);
    }
}
