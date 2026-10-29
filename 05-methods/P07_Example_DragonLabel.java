/// # Worked example · 5.3 Return a result to the caller
/// Lesson: 03-results.html

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
