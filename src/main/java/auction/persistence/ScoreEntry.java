package auction.persistence;

import java.util.UUID;

public class ScoreEntry {
    private final String sessionId;
    private final String name;
    private final int balance;
    public ScoreEntry(String sessionId, String name, int balance) {
        if (name == null || name.isBlank() || balance < 0) throw new IllegalArgumentException("Invalid score.");
        this.sessionId = UUID.fromString(sessionId).toString(); this.name = name; this.balance = balance;
    }
    public String getSessionId() { return sessionId; }
    public String getName() { return name; }
    public int getBalance() { return balance; }
    @Override public String toString() { return name + ": $" + balance; }
}
