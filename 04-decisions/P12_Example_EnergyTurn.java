/// # Worked example · 4.4 Stop an illegal move in time
/// Lesson: 04-guards.html

void main() {
    int energy = 5;
    int cost = Integer.parseInt(IO.readln("Move cost: ").strip());
    if (cost < 1) {
        IO.println("Cost must be positive");
        IO.println("Energy: " + energy);
        return;
    }
    if (cost > energy) {
        IO.println("Not enough energy");
        IO.println("Energy: " + energy);
        return;
    }
    energy = energy - cost;
    IO.println("Move accepted");
    IO.println("Energy: " + energy);
}
