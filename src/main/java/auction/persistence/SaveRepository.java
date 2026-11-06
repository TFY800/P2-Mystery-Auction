package auction.persistence;

import auction.domain.AuctionRecord;
import auction.domain.GameSession;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

public class SaveRepository {
    private final Path path;
    public SaveRepository(Path path) {
        if (path == null) throw new IllegalArgumentException("Save path is required.");
        this.path = path.toAbsolutePath();
    }
    public boolean exists() { return Files.exists(path); }
    public void save(GameSession session, boolean overwrite) throws IOException {
        if (session == null || !session.isBetweenRounds()) {
            throw new IllegalStateException("Save only between rounds.");
        }
        if (!overwrite && exists()) throw new FileAlreadyExistsException(path.toString());
        Files.createDirectories(path.getParent());
        Path temporary = Files.createTempFile(path.getParent(), "save-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                writer.write("MYSTERY-SAVE-1"); writer.newLine();
                writer.write(session.getId() + "\t" + TextFormat.encode(session.getPlayer().getName()) + "\t"
                        + session.getPlayer().getBalance() + "\t" + session.getNextRound()); writer.newLine();
                for (AuctionRecord r : session.getHistory()) {
                    writer.write(r.getRound() + "\t" + r.getWinner() + "\t" + r.getWinningBid() + "\t"
                            + r.getResaleValue() + "\t" + TextFormat.encode(r.getIdentity())); writer.newLine();
                }
            }
            if (overwrite) Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
            else Files.move(temporary, path);
        } finally { Files.deleteIfExists(temporary); }
    }
    public GameSession load() throws IOException {
        if (!exists()) throw new IOException("Save file is missing: " + path);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            if (!"MYSTERY-SAVE-1".equals(reader.readLine())) throw new IllegalArgumentException("Invalid save header.");
            String[] header = TextFormat.fields(reader.readLine(), 4);
            List<AuctionRecord> records = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (records.size() == 10) throw new IllegalArgumentException("Too many rounds.");
                String[] f = TextFormat.fields(line, 5);
                records.add(new AuctionRecord(Integer.parseInt(f[0]), Integer.parseInt(f[1]),
                        Integer.parseInt(f[2]), Integer.parseInt(f[3]), TextFormat.decode(f[4])));
            }
            return GameSession.restore(header[0], TextFormat.decode(header[1]), Integer.parseInt(header[2]),
                    Integer.parseInt(header[3]), records);
        } catch (IllegalArgumentException | ArithmeticException e) {
            throw new IOException("Corrupt save: " + e.getMessage(), e);
        }
    }
}
