/// # Úkol · 4.4 tah je odmítnutý, ale energie zmizela
/// Lekce: 04-guards.html

void main() {
    int energy = 5;
    int cost = Integer.parseInt(IO.readln("Move cost: ").strip());
    energy = energy - cost;
    if (cost < 1 || cost > 5) {
        IO.println("Move refused");
    }
    IO.println("Move accepted");
    IO.println("Energy: " + energy);
}
