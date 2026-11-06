package auction.persistence;

import auction.domain.GameSession;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeaderboardRepository {
    private final Path path;
    public LeaderboardRepository(Path path) {
        if (path == null) throw new IllegalArgumentException("Leaderboard path is required.");
        this.path = path.toAbsolutePath();
    }
    public List<ScoreEntry> load() throws IOException {
        List<ScoreEntry> scores = new ArrayList<>();
        if (!Files.exists(path)) return scores;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            if (!"MYSTERY-SCORES-1".equals(reader.readLine())) throw new IllegalArgumentException("Invalid leaderboard header.");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = TextFormat.fields(line, 3);
                ScoreEntry entry = new ScoreEntry(f[0], TextFormat.decode(f[1]), Integer.parseInt(f[2]));
                for (ScoreEntry previous : scores) {
                    if (previous.getSessionId().equals(entry.getSessionId())) {
                        throw new IllegalArgumentException("Duplicate session in leaderboard.");
                    }
                }
                scores.add(entry);
            }
        } catch (IllegalArgumentException e) { throw new IOException("Corrupt leaderboard: " + e.getMessage(), e); }
        scores.sort(Comparator.comparingInt(ScoreEntry::getBalance).reversed());
        return scores;
    }
    public boolean record(GameSession session) throws IOException {
        if (session == null || !session.isComplete() || !session.isBetweenRounds()) {
            throw new IllegalStateException("Only completed sessions can be recorded.");
        }
        List<ScoreEntry> scores = load();
        for (ScoreEntry score : scores) if (score.getSessionId().equals(session.getId())) return false;
        scores.add(new ScoreEntry(session.getId(), session.getPlayer().getName(), session.getPlayer().getBalance()));
        scores.sort(Comparator.comparingInt(ScoreEntry::getBalance).reversed());
        Files.createDirectories(path.getParent());
        Path temporary = Files.createTempFile(path.getParent(), "scores-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                writer.write("MYSTERY-SCORES-1"); writer.newLine();
                for (ScoreEntry score : scores) {
                    writer.write(score.getSessionId() + "\t" + TextFormat.encode(score.getName()) + "\t" + score.getBalance());
                    writer.newLine();
                }
            }
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
        } finally { Files.deleteIfExists(temporary); }
        return true;
    }
}
