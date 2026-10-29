/// # Hotový příklad · 6.1 Opakuj, dokud je potřeba
/// Lekce: 01-while.html

void main() {
    int energy = 0;
    while (energy < 6) {
        energy = energy + 2;
        IO.println("Energy: " + energy);
    }
    IO.println("Ready");
}
