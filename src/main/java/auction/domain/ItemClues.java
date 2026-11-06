package auction.domain;

public class ItemClues {
    private final String template;
    private final int size;
    private final int weight;
    private final int age;
    private final String material;
    private final int condition;

    public ItemClues(String template, int size, int weight, int age, String material, int condition) {
        if (template == null || template.isBlank() || material == null || material.isBlank()
                || size < 1 || size > 3 || weight < 1 || weight > 20
                || age < 1 || age > 100 || condition < 1 || condition > 5) {
            throw new IllegalArgumentException("Invalid item clues.");
        }
        this.template = template; this.size = size; this.weight = weight;
        this.age = age; this.material = material; this.condition = condition;
    }
    // Shared public appraisal guide; opponents add uncertainty to this guide.
    public int guideValue() {
        int materialBonus = material.equals("brass") ? 70 : 20;
        return 40 + size * 20 + weight * 4 + age * 2 + condition * 30 + materialBonus;
    }
    @Override public String toString() {
        return template + " | Size: " + size + "/3 | Weight: " + weight + " kg | Approximate age: "
                + age + " years | Material: " + material + " | Condition: " + condition + "/5";
    }
}
