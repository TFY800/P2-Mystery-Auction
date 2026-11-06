package auction.domain;

public class AuctionItem {
    private final ItemClues clues;
    private final String identity;
    private final int hiddenValue;
    public AuctionItem(ItemClues clues, String identity) {
        if (clues == null || identity == null || identity.isBlank()) {
            throw new IllegalArgumentException("Invalid item.");
        }
        this.clues = clues; this.identity = identity;
        hiddenValue = clues.guideValue();
    }
    public ItemClues getClues() { return clues; }
    String getIdentity() { return identity; }
    int getHiddenValue() { return hiddenValue; }
}
