/// # Worked example · 6.1 Repeat while needed
/// Lesson: 01-while.html

void main() {
    int energy = 0;
    while (energy < 6) {
        energy = energy + 2;
        IO.println("Energy: " + energy);
    }
    IO.println("Ready");
}
