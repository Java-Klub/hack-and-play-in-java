/// # Hotový příklad · 5.4 Kam až dosáhne jméno
/// Lekce: 04-local-values.html

void main() {
    int energy = 3;
    drain(energy);
    IO.println("In main: " + energy);
}

void drain(int energy) {
    energy = energy - 1;
    IO.println("In drain: " + energy);
}
