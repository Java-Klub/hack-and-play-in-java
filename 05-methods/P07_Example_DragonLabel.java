/// # Hotový příklad · 5.3 Vrať výsledek volajícímu
/// Lekce: 03-results.html

void main() {
    int strength = Integer.parseInt(IO.readln("Strength: ").strip());
    String label = dragonName(strength);
    IO.println("You found: " + label);
}

String dragonName(int strength) {
    if (strength >= 5) {
        return "Ancient dragon";
    }
    return "Baby dragon";
}
