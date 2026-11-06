package auction.domain;

public class Player {
    private final String name;
    private int balance;

    public Player(String name) { this(name, 1000); }
    public Player(String name, int balance) {
        if (name == null || name.isBlank() || balance < 0) {
            throw new IllegalArgumentException("Name must be nonblank and balance nonnegative.");
        }
        this.name = name;
        this.balance = balance;
    }
    public String getName() { return name; }
    public int getBalance() { return balance; }
    void settle(int price, int resale) {
        if (price < 0 || price > balance || resale < 0) {
            throw new IllegalArgumentException("Invalid settlement.");
        }
        balance = Math.addExact(balance - price, resale);
    }
}
