/// # Worked example · 6.3 Remember what has passed
/// Lesson: 03-accumulators.html

void main() {
    int total = 0;
    for (int delivery = 0; delivery < 3; delivery++) {
        int amount = Integer.parseInt(IO.readln("Fuel: ").strip());
        total = total + amount;
    }
    IO.println("Fuel total: " + total);
}
