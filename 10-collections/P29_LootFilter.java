void main() {
    List<Loot> loot = List.of(new Loot("Stone", 0), new Loot("Gem", 5),
        new Loot("Coin", 1), new Loot("Debt", -2));
    IO.println("Names: " + valuableNames(loot));
    IO.println("Empty: " + valuableNames(List.of()));
    IO.println("Original size: " + loot.size());
}

List<String> valuableNames(List<Loot> loot) {
    return List.of();
}

record Loot(String name, int value) {
}
