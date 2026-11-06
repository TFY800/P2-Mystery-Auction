package auction;

import auction.ui.ConsoleUI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Random;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class ConsoleTest {
    @TempDir Path directory;
    private String run(String input) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (Scanner scanner = new Scanner(input);
             PrintStream stream = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(stream);
            new ConsoleUI(scanner, directory, new Random(12)).run();
        } finally { System.setOut(original); }
        return output.toString(StandardCharsets.UTF_8);
    }
    @Test void newInvalidInputBidWithdrawalSaveLoadAndCompletion() throws Exception {
        String first = run("n\n\nn\nAlice\np\nhello\n49\n1001\n50\nw\ns\nq\n");
        assertTrue(first.contains("Name must be nonblank"));
        assertTrue(first.contains("whole-dollar")); assertTrue(first.contains("too small"));
        assertTrue(first.contains("Insufficient funds")); assertTrue(first.contains("Saved."));
        assertTrue(Files.exists(directory.resolve("save.txt")));
        StringBuilder input = new StringBuilder("l\nh\no\n");
        for (int i = 0; i < 9; i++) input.append("p\nw\n");
        input.append("q\n");
        String second = run(input.toString());
        assertTrue(second.contains("Next round: 2/10"));
        assertTrue(second.contains("Session complete. Final balance:"));
        assertTrue(second.contains("Leaderboard"));
        assertEquals(2, Files.readAllLines(directory.resolve("leaderboard.txt")).size());
    }
    @Test void overwriteNeedsExplicitYes() throws Exception {
        run("n\nFirst\ns\nq\n"); String original = Files.readString(directory.resolve("save.txt"));
        String cancelled = run("n\nSecond\ns\nno\ns\nno\n");
        assertTrue(cancelled.contains("Save cancelled")); assertEquals(original, Files.readString(directory.resolve("save.txt")));
        run("n\nSecond\ns\nyes\nq\n");
        assertNotEquals(original, Files.readString(directory.resolve("save.txt")));
    }
    @Test void missingAndCorruptLoadRemainVisible() throws Exception {
        assertTrue(run("l\nq\n").contains("missing"));
        Files.writeString(directory.resolve("save.txt"), "broken");
        assertTrue(run("l\nq\n").contains("Corrupt save"));
        assertEquals("broken", Files.readString(directory.resolve("save.txt")));
    }
}
