package auction.service;

import auction.domain.AuctionItem;
import auction.domain.ItemClues;
import java.util.Random;

public class ItemGenerator {
    private final Random random;
    private final String[] templates = {"Sealed instrument case", "Wrapped decorative object", "Dusty storage box"};
    private final String[] identities = {"Vintage survey instrument", "Collectible sculpture", "Antique mechanical toy"};
    public ItemGenerator(Random random) {
        if (random == null) throw new IllegalArgumentException("Random is required.");
        this.random = random;
    }
    public AuctionItem generate() {
        int template = random.nextInt(templates.length);
        ItemClues clues = new ItemClues(templates[template], 1 + random.nextInt(3), 1 + random.nextInt(20),
                1 + random.nextInt(100), random.nextBoolean() ? "brass" : "wood", 1 + random.nextInt(5));
        return new AuctionItem(clues, identities[template]);
    }
}
